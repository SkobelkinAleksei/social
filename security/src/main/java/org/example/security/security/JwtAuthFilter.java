package org.example.security.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.user.UserDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final AuthUtil authUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.split("Bearer ")[1];
            String email = authUtil.getUsernameFromToken(token);
            Long userId = authUtil.getUserIdFromToken(token);
            Collection<? extends GrantedAuthority> authorities = authUtil.getAuthoritiesFromToken(token);

            log.debug("[DEBUG] Найден токен. email={}, userId={}", email, userId);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // ✅ Создаем UserDto
                UserDto userDto = new UserDto();
                userDto.setUserId(userId);
                userDto.setEmail(email);

                // ✅ UserDto напрямую в principal + authorities
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDto,  // Principal = UserDto
                                null,     // credentials
                                authorities  // authorities
                        );

                // details для дополнительной информации
                Map<String, Object> details = new HashMap<>();
                details.put("userId", userId);
                details.put("email", email);
                authToken.setDetails(details);

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("[DEBUG] Аутентификация установлена с userId={}", userId);
            }
        } catch (Exception e) {
            log.warn("[WARN] Ошибка при парсинге JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
