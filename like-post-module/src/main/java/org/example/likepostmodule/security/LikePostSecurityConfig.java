package org.example.likepostmodule.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class LikePostSecurityConfig {
    private final LikePostJwtAuthFilter likePostJwtAuthFilter;  // ← ТВОЙ фильтр!

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth
                            // Для post-module ТОЛЬКО твои эндпоинты
                            .requestMatchers("/api/v1/social/likes/**").hasAnyRole("USER", "ADMIN")
                            .anyRequest().permitAll();  // ← УПРОЩЕННО!
                })
                .addFilterAfter(likePostJwtAuthFilter, UsernamePasswordAuthenticationFilter.class)  // ← ТВОЙ фильтр!
                .build();
    }
}
