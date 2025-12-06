package org.example.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RateLimiterConfig {

    @Value("${security.jwt.access-secret}")
    private String accessSecret;

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(extractSubject(exchange.getRequest().getHeaders().getFirst("Authorization")));
    }

    private String extractSubject(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return "anonymous";

        String token = authHeader.substring(7);

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(accessSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
