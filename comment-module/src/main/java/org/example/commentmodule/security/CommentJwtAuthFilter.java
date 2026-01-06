package org.example.commentmodule.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.user.UserDto;
import org.example.common.security.JwtHolder;
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
public class CommentJwtAuthFilter extends OncePerRequestFilter {
    private final CommentAuthUtil commentAuthUtil;
    private final JwtHolder jwtHolder;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        String safeLog = authHeader != null ?
                (authHeader.length() > 30 ? authHeader.substring(0, 30) + "..." : authHeader) : "null";
        log.debug("CommentJwtAuthFilter: токен '{}'", safeLog);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String email = commentAuthUtil.getUsernameFromToken(token);
            Long userId = commentAuthUtil.getUserIdFromToken(token);
            Collection<? extends GrantedAuthority> authorities = commentAuthUtil.getAuthoritiesFromToken(token);

            log.debug("Comment-module: токен распарсен. email={}, userId={}", email, userId);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDto userDto = new UserDto();
                userDto.setUserId(userId);
                userDto.setEmail(email);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDto, null, authorities);

                Map<String, Object> details = new HashMap<>();
                details.put("userId", userId);
                details.put("email", email);
                authToken.setDetails(details);

                jwtHolder.setToken(token);
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("Comment-module: аутентификация установлена userId={}", userId);
            }
        } catch (Exception e) {
            handleJwtError(response, e);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void handleJwtError(HttpServletResponse response, Exception e) throws IOException {
        String message;

        if (e instanceof io.jsonwebtoken.MalformedJwtException) {
            log.warn("Неверный формат JWT");
            message = "Неверный формат токена";
        }
        else if (e instanceof io.jsonwebtoken.ExpiredJwtException) {
            log.warn("JWT токен истёк");
            message = "Токен истёк";
        }
        else if (e instanceof io.jsonwebtoken.SignatureException) {
            log.warn("Неверная подпись JWT");
            message = "Неверная подпись токена";
        }
        else if (e instanceof io.jsonwebtoken.UnsupportedJwtException) {
            log.warn("Неподдерживаемый JWT");
            message = "Неподдерживаемый токен";
        }
        else {
            log.error("Ошибка JWT парсинга: {}", e.getMessage());
            message = "Ошибка авторизации";
        }

        sendErrorResponse(response, 401, message);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> error = new HashMap<>();
        error.put("status", status);
        error.put("error", "Unauthorized");
        error.put("message", message);

        new com.fasterxml.jackson.databind.ObjectMapper()
                .writeValue(response.getOutputStream(), error);
    }
}
