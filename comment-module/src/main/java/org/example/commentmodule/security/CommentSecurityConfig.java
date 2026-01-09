package org.example.commentmodule.security;

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
public class CommentSecurityConfig {
    private final CommentJwtAuthFilter commentJwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Твои эндпоинты
                        .requestMatchers("/api/v1/social/comments/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/social/comments/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .addFilterAfter(commentJwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
