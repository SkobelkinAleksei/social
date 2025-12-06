package org.example.usermodule.entity.enums.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.example.usermodule.entity.enums.Role;

import static java.util.Objects.nonNull;

@Converter
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        if (nonNull(role)) return role.name();
        return null;
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {
        if (nonNull(dbData)) return Role.fromString(dbData);
        return null;
    }
}