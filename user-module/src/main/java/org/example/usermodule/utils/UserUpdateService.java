package org.example.usermodule.utils;

import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.UpdateAccountUserDto;
import org.example.usermodule.dto.UpdatePasswordUserDto;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.entity.enums.UserEntity;
import org.example.usermodule.mapper.UserMapper;
import org.example.usermodule.repository.UserRepository;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class UserUpdateService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto updateAccount(UserEntity userEntity, UpdateAccountUserDto updateAccountUser) {

        if (isNull(updateAccountUser)) {
            throw new NullPointerException("UpdateAccountUserDto: " + updateAccountUser + "невозможно обновить!");
        }

        if (!isNull(updateAccountUser.getUsername())) {
            userEntity.setUsername(updateAccountUser.getUsername());
        }

        if (!isNull(updateAccountUser.getLastName())) {
            userEntity.setLastName(updateAccountUser.getLastName());
        }

        if (!isNull(updateAccountUser.getEmail())) {
            userEntity.setEmail(updateAccountUser.getEmail());
        }

        if (!isNull(updateAccountUser.getNumberPhone())) {
            userEntity.setNumberPhone(updateAccountUser.getNumberPhone());
        }

        if (!isNull(updateAccountUser.getBirthday())) {
            userEntity.setBirthday(updateAccountUser.getBirthday());
        }

        return userMapper.toDto(userEntity);
    }

    public void updatePassword(
            UserEntity userEntity,
            UpdatePasswordUserDto updatePasswordUserDto
    ) {
        if (!updatePasswordUserDto.getOldPassword().equals(userEntity.getPassword())) {
            throw new IllegalArgumentException("Неверный старый пароль");
        }

        userEntity.setPassword(updatePasswordUserDto.getNewPassword());
        userRepository.save(userEntity);
    }
}
