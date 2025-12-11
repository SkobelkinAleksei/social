package org.example.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final SecurityProperties securityProperties;

    private SecretKey getAccessKey() {
        return Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(securityProperties.getAccessSecret())
        );
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
}
