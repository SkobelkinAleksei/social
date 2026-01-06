package org.example.security.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.common.dto.TokenGenerationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class LoginUtil {
    @Value("${security.jwt.refresh-secret}")
    private String jwtSecret;

    @Value("${security.jwt.expiration:3600000}")
    private Long expiration;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(TokenGenerationRequest request) {
        return Jwts.builder()
                .setSubject(request.email())
                .claim("userId", request.userId())
                .claim("role", request.role())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey())
                .compact();
    }
}
