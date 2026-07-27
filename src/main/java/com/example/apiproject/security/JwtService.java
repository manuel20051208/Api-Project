package com.example.apiproject.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("Configura jwt.secret.key con una llave Base64 de minimo 32 bytes");
        }
        byte[] keyBytes = Base64.getDecoder().decode(secretKey.trim());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAdminToken(Long userId, String email, String accountType) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("id", userId)
                .claim("accountType", accountType)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateClientToken(Long userId, String email, String accountType) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("id", userId)
                .claim("accountType", accountType)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public Optional<AuthenticatedUser> parseAuthenticatedUser(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (claims.getExpiration().before(new Date())) {
                return Optional.empty();
            }

            Number id = claims.get("id", Number.class);
            String subject = claims.getSubject();
            String accountType = claims.get("accountType", String.class);

            if (id == null || subject == null || accountType == null) {
                return Optional.empty();
            }

            return Optional.of(AuthenticatedUser.fromToken(id.longValue(), subject, accountType));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
