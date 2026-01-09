package org.example.usermodule.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.JwtUserData;
import org.example.usermodule.entity.UserEntity;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final SecurityProperties securityProperties;

    private SecretKey getAccessKey() {
        return Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(securityProperties.getAccessSecret())
        );
    }

    private SecretKey getRefreshKey() {
        return Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(securityProperties.getRefreshSecret())
        );
    }

    public String generateAccessToken(UserEntity user) {
        return Jwts.builder()
                .claim("id", user.getId())
                .claim("role", user.getRole().name())
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(getAccessKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserEntity user) {
        return Jwts.builder()
                .claim("id", user.getId())
                .setExpiration(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
                .signWith(getRefreshKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtUserData validateAccessToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getAccessKey())
                .parseClaimsJws(token)
                .getBody();

        Long id = claims.get("id", Long.class);
        String role = claims.get("role", String.class);

        return new JwtUserData(id, role);
    }

    public Long validateRefreshToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getRefreshKey())
                .parseClaimsJws(token)
                .getBody();

        return claims.get("id", Long.class);
    }
}
