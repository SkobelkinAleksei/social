package org.example.common.security;

import lombok.experimental.UtilityClass;
import org.example.common.dto.UserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new IllegalStateException("Пользователь не авторизован");
        }

        Object principal = auth.getPrincipal();
        if (!(principal instanceof UserDto userDto)) {
            throw new IllegalStateException("Principal не является UserEntity");
        }
        return userDto.getUserId();
    }

    public static UserDto getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не авторизован");
        }
        return (UserDto) auth.getPrincipal();
    }
}
