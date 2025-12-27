//package org.example.postmodule.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
//import org.springframework.security.web.SecurityFilterChain;
//
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//
//@RequiredArgsConstructor
//@EnableWebSecurity
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(AbstractHttpConfigurer::disable)
//                .sessionManagement(session -> {
//                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//                })
//                .authorizeHttpRequests(auth -> {
//                    auth.requestMatchers("/social/public/v1/auth/**")
//                            .permitAll()
////                            .requestMatchers("/api/v1/social/posts/**").hasAnyRole("USER", "ADMIN")
////                            .requestMatchers("/api/v1/social/admin/**").hasRole("ADMIN")
//                            .anyRequest()
//                            .authenticated();
//                })
//
//                .oauth2ResourceServer(
//                        oauth -> oauth.jwt(
//                                jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
//                )
//                .build();
//    }
//
//    @Bean
//    JwtDecoder jwtDecoder() {
//        SecretKey secretKey = new SecretKeySpec(
//                "KRLeMz77TA1D+uEo4iJbYJcaGgUy6IDzrPw440tAkJU+7cfLjtau8JHnoIAIRvUwRrOMtWq96HX2NYa0YUYKGQ==".getBytes(),
//                "HmacSHA256");
//        return NimbusJwtDecoder.withSecretKey(secretKey).build();
//    }
//
//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
//        converter.setAuthorityPrefix("ROLE_");
//        converter.setAuthoritiesClaimName("role");
//
//        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
//        authenticationConverter.setJwtGrantedAuthoritiesConverter(converter);
//
//        return authenticationConverter;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
