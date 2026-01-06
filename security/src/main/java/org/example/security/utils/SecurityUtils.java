package org.example.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.example.security.security.AuthUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    private final AuthUtil authUtil;

    public SecurityUtils(AuthUtil authUtil) {
        this.authUtil = authUtil;
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    public Long getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.split("Bearer ")[1];
            return authUtil.getUserIdFromToken(token);
        }
        return null;
    }
}