package org.example.usermodule.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
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

    private final SecurityUserProperties securityProperties;

    private SecretKey getRefreshKey() {
        return Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(securityProperties.getRefreshSecret())
        );
    }

    public String generateRefreshToken(UserEntity user) {
        return Jwts.builder()
                .claim("id", user.getId())
                .setExpiration(Date.from(
                        Instant.now().plus(30, ChronoUnit.DAYS)
                ))
                .signWith(getRefreshKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateRefreshToken(String token) {
        return Jwts.parser()
                .setSigningKey(getRefreshKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
