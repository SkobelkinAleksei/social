package org.example.apigateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.apigateway.security.SecurityProperties;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import io.jsonwebtoken.Jwts;

import java.util.Base64;

@RequiredArgsConstructor
@Configuration
public class RateLimiterConfig {

    private final SecurityProperties securityProperties;

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.just(resolveKey(exchange));
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
                            Base64.getDecoder().decode(securityProperties.getAccessSecret())
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


