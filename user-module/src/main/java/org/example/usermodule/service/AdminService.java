package org.example.usermodule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.usermodule.entity.enums.UserEntity;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.dto.UserFullDto;
import org.example.usermodule.mapper.UserMapper;
import org.example.usermodule.repository.UserRepository;
import org.example.usermodule.utils.UserLookupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserLookupService userLookupService;

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.info("[INFO] Админ запрашивает список всех пользователей");
        List<UserDto> users = userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
        log.info("[INFO] Найдено пользователей: {}", users.size());
        return users;
    }

//    @Cacheable(value = "userById", key = "#userId")
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        log.info("[INFO] Админ запрашивает пользователя по id: {}", userId);
        UserEntity userEntity = userLookupService.getById(userId);
        log.info("[INFO] Пользователь с id: {} успешно найден", userId);
        return userMapper.toDto(userEntity);
    }

//    @CacheEvict(value = "userById", key = "#userId")
    @Transactional
    public void deleteUserById(Long userId) {
        log.info("[INFO] Админ удаляет пользователя по id: {}", userId);
        UserEntity userEntity = userLookupService.getById(userId);
        userRepository.deleteById(userEntity.getId());
        log.info("[INFO] Пользователь с id: {} успешно удалён", userId);
    }

    @Transactional(readOnly = true)
    public UserFullDto getUserProfileById(Long userId) {
        log.info("[INFO] Админ запрашивает профиль пользователя по id: {}", userId);
        UserEntity userEntity = userLookupService.getById(userId);
        log.info("[INFO] Профиль пользователя с id: {} успешно получен", userId);
        return userMapper.toFullDto(userEntity);
    }
}
