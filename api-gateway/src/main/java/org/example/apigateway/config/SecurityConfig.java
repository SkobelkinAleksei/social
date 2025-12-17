package org.example.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.example.apigateway.security.SecurityProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@EnableWebFluxSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final SecurityProperties securityProperties;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers("/social/public/**").permitAll()
                        // Роуты, доступные только пользователям с ролью 'ADMIN'
                        .pathMatchers("/social/v1/admin/**").hasRole("ADMIN")
                        // Роуты, доступные только пользователям с ролью 'USER'
                        .pathMatchers("/social/v1/users/**").hasAnyRole("ADMIN", "USER")

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder
                .withSecretKey(
                        new SecretKeySpec(
                                Base64.getDecoder().decode(securityProperties.getAccessSecret()),
                                "HmacSHA256"
                        )
                )
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");
            if (role != null) {
                return Flux.just(new SimpleGrantedAuthority("ROLE_" + role));  // Добавляем "ROLE_" тут
            }
            return Flux.empty();
        });

        return converter;
    }

}

