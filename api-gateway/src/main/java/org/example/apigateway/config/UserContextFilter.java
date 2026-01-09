//package org.example.apigateway.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Slf4j
//@Component
//public class UserContextFilter implements GlobalFilter {
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String path = exchange.getRequest().getPath().value();
//
//        log.info("[INFO] {}", path);
//        // Пропускаем публичные эндпоинты
//        if (path.startsWith("/social/public/**")) {
//            return chain.filter(exchange);
//        }
//
//        return exchange.getPrincipal()
//                .cast(JwtAuthenticationToken.class)
//                .map(auth -> {
//                    var jwt = auth.getToken();
//                    // Логируем добавление заголовков
//                    System.out.println("Adding headers X-User-Id: " + jwt.getSubject() + ", X-User-Role: " + "ROLE_" + jwt.getClaimAsString("role"));
//
//                    var request = exchange.getRequest().mutate()
//                            .header("X-User-Id", jwt.getSubject())
//                            .header("X-User-Role", "ROLE_" + jwt.getClaimAsString("role"))
//                            .build();
//                    return exchange.mutate().request(request).build();
//                })
//                .defaultIfEmpty(exchange)
//                .flatMap(chain::filter);
//    }
//}
//
//
