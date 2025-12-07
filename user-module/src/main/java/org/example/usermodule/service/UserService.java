package org.example.usermodule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.UpdateAccountUserDto;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.entity.UserEntity;
import org.example.usermodule.mapper.UserMapper;
import org.example.usermodule.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Cacheable(value = "userByEmail", key = "#email")
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        System.out.println("Запрос в базу!");
        return userMapper.toDto(
                userRepository.findByEmailIgnoreCase(email)
                        .orElseThrow(() -> new EntityNotFoundException("Пользователь не был найден")));
    }

    @CacheEvict(value = "userById", key = "#userId")
    @Transactional
    public UserDto updateUserAccount(Long userId, UpdateAccountUserDto updateAccountUser) throws AccessDeniedException {
        Long authId = (Long) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!authId.equals(userId)) {
            throw new AccessDeniedException("Вы не можете обновить чужой аккаунт");
        }

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь не был найден.")
        );

        if (!passwordEncoder.matches(updateAccountUser.getOldPassword(), userEntity.getPassword())) {
            throw new IllegalArgumentException("Неверный старый пароль");
        }

        if (!isNull(updateAccountUser.getUsername())) {
            userEntity.setUsername(updateAccountUser.getUsername());
        }

        if (!isNull(updateAccountUser.getLastName())) {
            userEntity.setLastName(updateAccountUser.getLastName());
        }

        if (!isNull(updateAccountUser.getEmail())) {
            Optional<UserEntity> byUserEmail  = userRepository.findByEmailIgnoreCase(updateAccountUser.getEmail());
            if (byUserEmail.isPresent() && !byUserEmail.get().getId().equals(userId)) {
                throw new IllegalArgumentException("Данный email уже занят");
            }
            userEntity.setEmail(updateAccountUser.getEmail());
        }

        if (!isNull(updateAccountUser.getNumberPhone())) {
            Optional<UserEntity> byNumberPhone = userRepository.findByNumberPhone(updateAccountUser.getNumberPhone());
            if (byNumberPhone.isPresent() && !byNumberPhone.get().getId().equals(userId)) {
                throw new IllegalArgumentException("Данный номер телефона уже занят");
            }
            userEntity.setNumberPhone(updateAccountUser.getNumberPhone());
        }

        if (!isNull(updateAccountUser.getNewPassword())) {
            String newPass = passwordEncoder.encode(updateAccountUser.getNewPassword());
            userEntity.setPassword(newPass);
        }

        if (!isNull(updateAccountUser.getBirthday())) {
            userEntity.setBirthday(updateAccountUser.getBirthday());
        }

        return userMapper.toDto(
                userRepository.save(userEntity)
        );
    }
}
