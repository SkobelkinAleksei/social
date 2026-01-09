package org.example.usermodule.mapper;

import org.example.usermodule.dto.UserFullDto;
import org.example.usermodule.dto.authDto.RegistrationUserDto;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "timeStamp", ignore = true)
    UserEntity toEntity(RegistrationUserDto userDto);

    UserEntity toEntity(UserDto userDto);

    UserFullDto toFullDto(UserEntity userEntity);

    UserDto toDto(UserEntity userEntity);
}