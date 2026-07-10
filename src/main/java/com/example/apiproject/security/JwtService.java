package com.example.apiproject.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private Key getSigningKey() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("Configura jwt.secret.key con una llave Base64 de minimo 32 bytes");
        }
        byte[] keyBytes = Base64.getDecoder().decode(secretKey.trim());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAdminToken(Long userId, String username, String accountType) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("id", userId)
                .claim("accountType", accountType)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateClientToken(Long userId, String email, String accountType) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(email)
                .claim("id", userId)
                    .claim("accountType", accountType)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Optional<AuthenticatedUser> parseAuthenticatedUser(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (claims.getExpiration().before(new Date())) {
                return Optional.empty();
            }

            Number id = claims.get("id", Number.class);
            String username = claims.getSubject();
            String accountType = claims.get("accountType", String.class);

            if (id == null || username == null || accountType == null) {
                return Optional.empty();
            }

            return Optional.of(AuthenticatedUser.fromToken(id.longValue(), username, accountType));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}