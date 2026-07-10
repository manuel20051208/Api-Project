package com.example.apiproject.config;

import com.example.apiproject.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
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
                                "/actuator/health"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/user/login",
                                "/api/user/register",
                                "/api/client/login",
                                "/api/client/register"
                        ).permitAll()
                        // Productos e información pública del administrador por ID
                        .requestMatchers(HttpMethod.GET,
                                "/api/product/search/active-with-images",
                                "/api/product/search/id/**",
                                "/api/product/search/category/**",
                                "/api/product/search/name/**",
                                "/api/product/activeProducts",
                                "/api/product/*/admin",
                                "/api/user/*/admin"
                        ).permitAll()
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
                                "/api/product/search/with-images",
                                "/api/product/search/with-images"
                        ).hasRole("ADMIN")
                        .requestMatchers("/api/product/**").hasRole("ADMIN")
                        .requestMatchers("/api/sale/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/notification/stream")
                        .hasRole("ADMIN")
                        .anyRequest().authenticated()
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
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
