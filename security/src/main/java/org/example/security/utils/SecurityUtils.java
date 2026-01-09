package org.example.security.utils;

import org.example.security.security.AuthUtil;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    private final AuthUtil authUtil;

    public SecurityUtils(AuthUtil authUtil) {
        this.authUtil = authUtil;
    }
}