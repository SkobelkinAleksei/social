package org.example.usermodule.config;

import lombok.RequiredArgsConstructor;
import org.example.usermodule.entity.enums.Role;
import org.example.usermodule.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/social/v1/public/auth/login",
                                "/social/v1/public/auth/registration",
                                "/social/v1/public/auth/refresh",
                                "/", "/error"
                        ).permitAll()

                        .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                        .requestMatchers(
                                "/social/v1/users/**"
                        ).hasAnyRole(Role.USER.name(), Role.ADMIN.name())

                        .anyRequest().authenticated()
                )

                // добавляем JWT-фильтр перед UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

