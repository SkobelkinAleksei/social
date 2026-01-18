//package org.example.postmodule.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@RequiredArgsConstructor
//@EnableWebSecurity
//@Configuration
//public class PostSecurityConfig {
//    private final PostJwtAuthFilter postJwtAuthFilter;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> {
//                    auth
//                            .requestMatchers("/api/v1/social/posts/**").hasAnyRole("USER", "ADMIN")
//                            .requestMatchers("/api/v1/social/admin/**").hasAnyRole("ADMIN")
//                            .requestMatchers("/api/v1/social/users/post/**").permitAll()
//                            .anyRequest().permitAll();
//                })
//                .addFilterAfter(postJwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }
//}
