package org.example.usermodule.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.UpdateAccountUserDto;
import org.example.usermodule.dto.UpdatePasswordUserDto;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.dto.UserFullDto;
import org.example.usermodule.entity.UserEntity;
import org.example.usermodule.mapper.UserMapper;
import org.example.usermodule.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('USER')")
    @Cacheable(value = "userByEmail", key = "#email")
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        System.out.println("Запрос в базу!");
        return userMapper.toDto(
                userRepository.findByEmailIgnoreCase(email)
                        .orElseThrow(() -> new EntityNotFoundException("Пользователь не был найден")));
    }

    @PreAuthorize("hasRole('USER')")
    @CacheEvict(value = {"userByEmail"}, allEntries = true)
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

        return userMapper.toDto(
                userRepository.save(userEntity)
        );
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional(readOnly = true)
    public UserFullDto getMyProfile(Long userId) throws AccessDeniedException {
        Long authId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!authId.equals(userId)) {
            throw new AccessDeniedException("Нет доступа к профилю!");
        }

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь не был найден.")
        );

        return userMapper.toFullDto(userEntity);
    }

    @PreAuthorize("hasRole('USER')")
    @Transactional(readOnly = true)
    public void updatePassword(
            Long userId,
            UpdatePasswordUserDto updatePasswordUserDto
    ) throws AccessDeniedException {

        Long authId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!authId.equals(userId)) {
            throw new AccessDeniedException("Нет доступа для изменения пароля!");
        }

        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь не был найден.")
        );

        if (!passwordEncoder.matches(updatePasswordUserDto.getOldPassword(), userEntity.getPassword())) {
            throw new IllegalArgumentException("Неверный старый пароль");
        }

        String newPass = passwordEncoder.encode(updatePasswordUserDto.getNewPassword());
        userEntity.setPassword(newPass);
    }
}
