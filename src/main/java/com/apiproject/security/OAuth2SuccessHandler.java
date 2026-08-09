package com.apiproject.security;

import com.apiproject.entities.admin.UserAdmin;
import com.apiproject.entities.client.UserClient;
import com.apiproject.repositories.admin.UserRepository;
import com.apiproject.repositories.client.ClientRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    private static final String ADMIN_REGISTRATION_ID = "google-admin";
    private static final String CLIENT_REGISTRATION_ID = "google-client";
    private static final String ADMIN_REDIRECT_URL = "http://localhost:3000/";
    private static final String CLIENT_REDIRECT_URL = "http://localhost:3000/portal";

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        // 1. Sacamos el registrationId ("google-admin" o "google-client")
        //    para saber qué flujo disparó este login.
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();

        // 2. Extraemos los datos que Google nos dio del usuario.
        OAuth2User oauthUser = oauthToken.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");

        if (!StringUtils.hasText(email)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Google no retorno un email valido");
            return;
        }

        String fullName = StringUtils.hasText(name) ? name : email;
        String token;
        String redirectUrl;
        String photo;

        if (ADMIN_REGISTRATION_ID.equals(registrationId)) {
            // 3a. Flujo ADMIN: buscamos o creamos el UserAdmin
            UserAdmin admin = userRepository.findByEmail(email)
                    .map(existing -> updateAdminGoogleProfile(existing, fullName, picture))
                    .orElseGet(() -> {
                        UserAdmin nuevo = new UserAdmin();
                        nuevo.setEmail(email);
                        nuevo.setFullName(fullName);
                        nuevo.setProfilePhotoUrl(picture);
                        nuevo.setPassword("");
                        nuevo.setBusinessName("");
                        return userRepository.save(nuevo);
                    });

            token = jwtService.generateAdminToken(admin.getId(), admin.getEmail(), "ADMIN");
            redirectUrl = ADMIN_REDIRECT_URL;
            photo = admin.getProfilePhotoUrl();

        } else if (CLIENT_REGISTRATION_ID.equals(registrationId)) {
            // 3b. Flujo CLIENT: buscamos o creamos el UserClient
            UserClient client = clientRepository.findByEmail(email)
                    .map(existing -> updateClientGoogleProfile(existing, fullName, picture))
                    .orElseGet(() -> {
                        UserClient nuevo = new UserClient();
                        nuevo.setEmail(email);
                        nuevo.setFullName(fullName);
                        nuevo.setPassword("");
                        nuevo.setPhoto(picture);
                        nuevo.setAddress("");
                        return clientRepository.save(nuevo);
                    });

            token = jwtService.generateClientToken(client.getId(), client.getEmail(), "CLIENT");
            redirectUrl = CLIENT_REDIRECT_URL;
            photo = client.getPhoto();

        } else {
            // Caso raro: registrationId desconocido
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Registro OAuth2 no reconocido");
            return;
        }

        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        // 4. Redirigimos al frontend con el JWT como query param
        response.sendRedirect(buildRedirectUrl(redirectUrl, token, photo));
    }

    private UserAdmin updateAdminGoogleProfile(UserAdmin admin, String name, String picture) {
        boolean changed = false;

        if (StringUtils.hasText(name) && !name.equals(admin.getFullName())) {
            admin.setFullName(name);
            changed = true;
        }

        if (StringUtils.hasText(picture) && !picture.equals(admin.getProfilePhotoUrl())
        || admin.getProfilePhotoUrl() == null) {
            admin.setProfilePhotoUrl(picture);
            changed = true;
        }

        return changed ? userRepository.save(admin) : admin;
    }

    private UserClient updateClientGoogleProfile(UserClient client, String name, String picture) {
        boolean changed = false;

        if (StringUtils.hasText(name) && !name.equals(client.getFullName())) {
            client.setFullName(name);
            changed = true;
        }
        if (StringUtils.hasText(picture) && !picture.equals(client.getPhoto())) {
            client.setPhoto(picture);
            changed = true;
        }

        return changed ? clientRepository.save(client) : client;
    }

    private String buildRedirectUrl(String redirectUrl, String token, String photo) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("token", token);

        if (StringUtils.hasText(photo)) {
            builder.queryParam("photo", photo);
        }

        return builder.build().encode().toUriString();
    }
}
