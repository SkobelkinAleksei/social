package org.example.apigateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(ex -> ex
                        .pathMatchers(
                                "/actuator/**",
                                "/social/public/**",
                                "/api-gateway/social/public/**",
                                "/error",
                                "/",
                                "/auth/**"
                        ).permitAll()

                        .pathMatchers("/social/v1/admin/**")
                        .hasRole("ADMIN")

                        .pathMatchers("/social/v1/users/**")
                        .hasAnyRole("USER", "ADMIN")

                        .pathMatchers("/social/v1/posts/**")
                        .hasRole("USER")

                        .anyExchange()
                        .authenticated()
                )

                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)

                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


