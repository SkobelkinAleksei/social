package org.example.apigateway.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.apigateway.security.SecurityProperties;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtAccessTokenService {

    private final SecurityProperties properties;

    public String generate(Long userId, String role) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(
                        Date.from(Instant.now().plus(15, ChronoUnit.MINUTES))
                )
                .signWith(
                        Keys.hmacShaKeyFor(
                                Base64.getDecoder().decode(properties.getAccessSecret())
                        ),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }
}
