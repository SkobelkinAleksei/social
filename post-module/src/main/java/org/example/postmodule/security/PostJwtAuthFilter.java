//package org.example.postmodule.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.example.common.dto.user.UserDto;
//import org.example.common.security.JwtHolder;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class PostJwtAuthFilter extends OncePerRequestFilter {
//    private final PostAuthUtil postAuthUtil;
//    private final JwtHolder jwtHolder;
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//        log.debug("🔍 PostJwtAuthFilter: проверка токена '{}'",
//                authHeader != null ? authHeader.substring(0, 30) + "..." : "null");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            String token = authHeader.split("Bearer ")[1];
//            String email = postAuthUtil.getUsernameFromToken(token);
//            Long userId = postAuthUtil.getUserIdFromToken(token);
//
//            // Минимальные authorities (для @PreAuthorize)
//            Collection<? extends GrantedAuthority> authorities = postAuthUtil.getAuthoritiesFromToken(token);
//
//            log.debug("[DEBUG] Post-module: токен распарсен. email={}, userId={}", email, userId);
//
//            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//                UserDto userDto = new UserDto();
//                userDto.setUserId(userId);
//                userDto.setEmail(email);
//
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(
//                                userDto,  // Principal = UserDto
//                                null,     // credentials
//                                authorities
//                        );
//
//                // Details для совместимости
//                Map<String, Object> details = new HashMap<>();
//                details.put("userId", userId);
//                details.put("email", email);
//                authToken.setDetails(details);
//                jwtHolder.setToken(token);
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//                log.debug("✅ Post-module: аутентификация установлена userId={}", userId);
//            }
//        } catch (Exception e) {
//            log.warn("⚠️ PostJwtAuthFilter: ошибка парсинга JWT: {}", e.getMessage());
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
