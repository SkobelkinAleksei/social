package org.example.apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;

@Configuration
public class RateLimiterConfig {

    @Value("${security.jwt.access-secret}")
    private String accessSecret;

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange ->
                Mono.just(resolveKey(exchange));
    }

    private String resolveKey(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        String subject = extractSubject(authHeader);

        return subject != null ? subject : "anonymous";
    }

    private String extractSubject(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return "anonymous";

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(
                            accessSecret.getBytes(StandardCharsets.UTF_8)
                    ))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        }
        catch (Exception e) {
            return "anonymous";
        }
    }
}

