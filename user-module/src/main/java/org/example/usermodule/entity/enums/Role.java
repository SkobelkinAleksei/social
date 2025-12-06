package org.example.usermodule.entity.enums;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public enum Role {
    ADMIN,
    USER;

    public SimpleGrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority("ROLE_" + this.name());
    }

    public static Role fromString(String value) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(value)) return role;
        }

        throw new IllegalArgumentException("Такая роль не была найдена: " + value);
    }
}
