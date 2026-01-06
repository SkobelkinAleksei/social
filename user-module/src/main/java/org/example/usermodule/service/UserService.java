package org.example.usermodule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.usermodule.entity.enums.UserEntity;
import org.example.usermodule.dto.*;
import org.example.usermodule.mapper.UserMapper;
import org.example.usermodule.repository.UserRepository;
import org.example.usermodule.utils.UserLookupService;
import org.example.usermodule.utils.UserSpecification;
import org.example.usermodule.utils.UserUpdateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserLookupService userLookupService;
    private final UserUpdateService userUpdateService;

//    @Cacheable(value = "userByEmail", key = "#email")
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        log.info("[INFO] Поиск пользователя по email: {}", email);
        return userMapper.toDto(
                userRepository.findByEmailIgnoreCase(email)
                        .orElseThrow(() -> {
                            log.warn("[INFO] Пользователь с email: {} не найден", email);
                            return new EntityNotFoundException("Пользователь не был найден");
                        }));
    }

//    @CacheEvict(value = {"userByEmail"}, allEntries = true)
    @Transactional
    public UserDto updateUserAccount(
            Long userId,
            UpdateAccountUserDto updateAccountUser
    ) {
        log.info("[INFO] Обновление аккаунта пользователя с id: {}", userId);
        UserEntity userEntity = userLookupService.getById(userId);

        UserDto userDto = userUpdateService.updateAccount(userEntity, updateAccountUser);
        log.info("[INFO] Аккаунт пользователя: {} успешно обновлён", userDto);

        return userDto;
    }

    @Transactional(readOnly = true)
    public UserFullDto getMyProfile(
            Long userId
    ) {
        log.info("[INFO] Получение профиля для пользователя с id: {}", userId);
        UserEntity userEntity = userLookupService.getById(userId);

        UserFullDto userFullDto = userMapper.toFullDto(userEntity);
        log.info("[INFO] Профиль пользователя: {} успешно получен", userFullDto);

        return userFullDto;
    }

    public void updatePassword(
            Long userId,
            UpdatePasswordUserDto updatePasswordUserDto
    ) {
        log.info("[INFO] Обновление пароля пользователя с id: {}", userId);
        UserEntity userEntity = userLookupService.getById(userId);

        userUpdateService.updatePassword(userEntity, updatePasswordUserDto);
        log.info("[INFO] Пароль пользователя с id: {} успешно обновлён", userId);
        userRepository.save(userEntity);
    }

    @Transactional(readOnly = true)
    public List<UserDto> searchUsers(UserFilterDto filter, int page, int size) {
        log.info("[INFO] Поиск пользователей с фильтром: {}, страница: {}, размер: {}",
                filter, page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<UserEntity> users = userRepository.findAll(
                UserSpecification.filter(filter),
                pageable
        );

        List<UserDto> dtoList = users.map(userMapper::toDto).toList();
        log.info("[INFO] Поиск пользователей завершён. Найдено записей: {}", dtoList.size());

        return dtoList;
    }

//    @Cacheable(value = "userById", key = "#userId")
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        log.info("[INFO] Получение пользователя по id: {}", userId);
        UserEntity userEntity = userLookupService.getById(userId);

        UserDto userDto = userMapper.toDto(userEntity);
        log.info("[INFO] Пользователь: {} успешно получен", userDto);

        return userDto;
    }
}
