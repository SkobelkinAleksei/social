package org.example.common.security;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.user.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

@Slf4j
@UtilityClass
public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Map<?, ?> details = auth.getDetails() instanceof Map ? (Map<?, ?>) auth.getDetails() : null;
        if (details != null && details.containsKey("userId")) {
            return (Long) details.get("userId");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDto userDto) {
            return userDto.getUserId();
        }
        return null;
    }

    public static UserDto getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        return principal instanceof UserDto ? (UserDto) principal : null;
    }
}
