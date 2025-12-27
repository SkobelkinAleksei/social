package org.example.usermodule.mapper;

import org.example.usermodule.entity.enums.UserEntity;
import org.example.usermodule.dto.UserFullDto;
import org.example.usermodule.dto.authDto.RegistrationUserDto;
import org.example.usermodule.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "timeStamp", ignore = true)
    UserEntity toEntity(RegistrationUserDto userDto);

//    UserEntity toEntity(UserDto userDto);

    UserFullDto toFullDto(UserEntity userEntity);

    @Mapping(target = "userId", source = "id")
    UserDto toDto(UserEntity userEntity);
}