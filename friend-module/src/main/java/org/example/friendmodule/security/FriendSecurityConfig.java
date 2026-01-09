package org.example.friendmodule.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class FriendSecurityConfig {
    private final FriendJwtAuthFilter friendJwtAuthFilter;

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Твои эндпоинты
                        .requestMatchers("/api/v1/social/friends/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().permitAll()
                )
                .addFilterAfter(friendJwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
