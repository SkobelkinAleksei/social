//package org.example.usermodule.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class UserModuleSecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())  // Отключаем CSRF с использованием нового подхода
//                .httpBasic(httpBasic -> httpBasic.disable())  // Отключаем базовую аутентификацию
//                .formLogin(formLogin -> formLogin.disable())  // Отключаем форму входа
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/social/public/v1/auth/**").permitAll()  // Публичные эндпоинты
//                        .requestMatchers("/social/v1/admin/**").hasAuthority("ROLE_ADMIN")  // Только для администраторов
//                        .requestMatchers("/social/v1/users/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
//                        .anyRequest().authenticated()  // Все остальные требуют аутентификации
//                );
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
