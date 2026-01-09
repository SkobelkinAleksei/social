//package org.example.apigateway.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.example.apigateway.service.JwtAccessTokenService;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import java.util.Map;
//
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("/social/public/v1/auth")
//public class GatewayAuthController {
//
//    private final WebClient webClient;
//    private final JwtAccessTokenService jwtAccessTokenService;
//
//    @PostMapping("/refresh")
//    public Mono<Map<String, String>> refresh(@RequestBody Map<String, String> body) {
//
//        return webClient.post()
//                .uri("lb://user-module/social/public/v1/auth/refresh")
//                .bodyValue(body)
//                .retrieve()
//                .bodyToMono(Map.class)
//                .map(resp -> {
//                    Long userId = Long.valueOf(resp.get("userId").toString());
//                    String role = resp.get("role").toString();
//
//                    String accessToken =
//                            jwtAccessTokenService.generate(userId, role);
//
//                    return Map.of("accessToken", accessToken);
//                });
//    }
//}
