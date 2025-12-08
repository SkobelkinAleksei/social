package org.example.usermodule.entity.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN,
    USER;

    public static Role fromString(String value) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(value)) return role;
        }

        throw new IllegalArgumentException("Такая роль не была найдена: " + value);
    }
}
