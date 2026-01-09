package org.example.usermodule.utils;

import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.UpdateAccountUserDto;
import org.example.usermodule.dto.UpdatePasswordUserDto;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.entity.enums.UserEntity;
import org.example.usermodule.mapper.UserMapper;
import org.example.usermodule.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static io.micrometer.common.util.StringUtils.isNotBlank;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class UserUpdateService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto updateAccount(UserEntity userEntity, UpdateAccountUserDto updateAccountUser) {

        if (isNull(updateAccountUser)) {
            throw new NullPointerException("UpdateAccountUserDto: " + updateAccountUser + "невозможно обновить!");
        }

        if (isNotBlank(updateAccountUser.getFirstName())) {
            userEntity.setFirstName(updateAccountUser.getFirstName());
        }

        if (isNotBlank(updateAccountUser.getLastName())) {
            userEntity.setLastName(updateAccountUser.getLastName());
        }

        if (isNotBlank(updateAccountUser.getEmail())) {
            userEntity.setEmail(updateAccountUser.getEmail());
        }

        if (isNotBlank(updateAccountUser.getNumberPhone())) {
            userEntity.setNumberPhone(updateAccountUser.getNumberPhone());
        }

        if (isNotBlank(String.valueOf(updateAccountUser.getBirthday()))) {
            userEntity.setBirthday(updateAccountUser.getBirthday());
        }

        return userMapper.toDto(userEntity);
    }

    public void updatePassword(
            UserEntity userEntity,
            UpdatePasswordUserDto updatePasswordUserDto
    ) {
        if (!passwordEncoder.matches(
                updatePasswordUserDto.getOldPassword(),
                userEntity.getPassword()
        )) {
            throw new IllegalArgumentException("Неверный старый пароль!");
        }
        String newPassword = updatePasswordUserDto.getNewPassword();
        userEntity.setPassword(
                passwordEncoder.encode(newPassword)
        );
    }
}
