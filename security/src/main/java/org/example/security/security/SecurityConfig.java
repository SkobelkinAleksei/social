package org.example.security.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/login", "/api/v1/auth/login").permitAll()
                            .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                            .requestMatchers("/social/public/v1/auth/**").permitAll()

                            // спец-эндпоинт для post-module (открытый)
                            .requestMatchers("/api/v1/social/users/post/**").permitAll()
                            .requestMatchers("/api/v1/social/friends/public/**").permitAll()

                            // остальные user-эндпоинты под ролями
                            .requestMatchers("/api/v1/social/users/**").hasAnyRole("USER", "ADMIN")
                            .requestMatchers("/api/v1/social/posts/**").hasAnyRole("USER","ADMIN")
                            .requestMatchers("/api/v1/social/friends/**").hasAnyRole("USER","ADMIN")
                            .requestMatchers("/api/v1/social/admin/**").hasRole("ADMIN")
                            .anyRequest().authenticated();
                })
                .addFilterAfter(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
