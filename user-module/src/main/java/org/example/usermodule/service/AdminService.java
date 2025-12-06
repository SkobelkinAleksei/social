package org.example.usermodule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.entity.UserEntity;
import org.example.usermodule.mapper.UserMapper;
import org.example.usermodule.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Cacheable(value = "userById", key = "#userId")
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Пользователь с id: " + userId + " не был найден")
                );

        return userMapper.toDto(userEntity);
    }

    @CacheEvict(value = "userById", key = "#userId")
    @Transactional
    public void deleteUserById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        userRepository.deleteById(user.getId());
    }
}
