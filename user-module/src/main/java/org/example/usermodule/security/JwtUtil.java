package org.example.usermodule.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.JwtUserData;
import org.example.usermodule.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final String accessSecret = "ACCESS_SECRET_KEY_1234567890";
    private final String refreshSecret = "REFRESH_SECRET_KEY_1234567890";

    public String generateAccessToken(UserEntity user) {
        return Jwts.builder()
                .claim("id", user.getId())
                .claim("role", user.getRole().name())
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(SignatureAlgorithm.HS256, accessSecret)
                .compact();
    }

    public String generateRefreshToken(UserEntity user) {
        return Jwts.builder()
                .claim("id", user.getId())
                .setExpiration(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
                .signWith(SignatureAlgorithm.HS256, refreshSecret)
                .compact();
    }

    public JwtUserData validateAccessToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(accessSecret)
                .parseClaimsJws(token)
                .getBody();

        Long id = claims.get("id", Long.class);
        String role = claims.get("role", String.class);

        return new JwtUserData(id, role);
    }

    public Long validateRefreshToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(refreshSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.get("id", Long.class);
    }
}
