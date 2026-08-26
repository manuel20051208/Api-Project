package com.apiproject.config;

import com.apiproject.security.JwtAuthenticationFilter;
import com.apiproject.security.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .securityContext(securityContext -> securityContext
                        .securityContextRepository(new RequestAttributeSecurityContextRepository()))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token requerido o invalido"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permisos para este recurso"))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/actuator/health",
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/user/login",
                                "/api/user/register",
                                "/api/client/login",
                                "/api/client/register"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/*/admin").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/product/search/active-with-images",
                                "/api/product/search/id/**",
                                "/api/product/search/category/**",
                                "/api/product/search/name/**",
                                "/api/product/activeProducts",
                                "/api/product/*/admin"
                        ).authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/product-images/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/user/upload-profile").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/client/upload-profile").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/client/profile-photo").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/product-images/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/product-images/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/sale/purchase").hasRole("CLIENT")
                        .requestMatchers("/api/client/**")
                        .hasRole("CLIENT")
                        .requestMatchers(
                                "/api/user/**",
                                "/dashboard-controller/**",
                                "/api/sales-items/**",
                                "/api/client-show-summary/**"
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/product/search",
                                "/api/product/search/with-images"
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/api/product/saveProduct",
                                "/api/product/deleteSafe"
                        ).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/product/update/**").hasRole("ADMIN")
                        .requestMatchers("/api/product/**").hasRole("ADMIN")
                        .requestMatchers("/api/sale/**").hasRole("ADMIN")
                        // ===== Cupones (validaciones accesibles a clientes, gestion solo ADMIN) =====
                        .requestMatchers(HttpMethod.GET,
                                "/api/cupons/validate",
                                "/api/sh-cupons/validate"
                        ).authenticated()
                        .requestMatchers(
                                "/api/cupons/**",
                                "/api/sh-cupons/**",
                                "/api/services-cupons/**"
                        ).hasRole("ADMIN")
                        // ===== Servicios ofrecidos =====
                        .requestMatchers(HttpMethod.GET, "/api/services/catalog/*").authenticated()
                        .requestMatchers("/api/services/**").hasRole("ADMIN")
                        // ===== Productos de segunda mano =====
                        .requestMatchers(HttpMethod.GET,
                                "/api/sh-product/search/active",
                                "/api/sh-product-images/**"
                        ).permitAll()
                        .requestMatchers("/api/sh-product/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/sh-product-images/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/sh-product-images/**").hasRole("ADMIN")
                        // ===== Ventas de segunda mano =====
                        .requestMatchers(HttpMethod.POST, "/api/sh-sale/purchase").hasRole("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/sh-sale/client").hasRole("CLIENT")
                        .requestMatchers("/api/sh-sale/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/notification/stream")
                        .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                )
                .oauth2Login(oauth2 ->
                        oauth2
                                .successHandler(oAuth2SuccessHandler)
                                .failureHandler((request,response,exception) ->
                                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error autenticando con google"))
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
