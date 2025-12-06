package org.example.usermodule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.mapper.UserMapper;
import org.example.usermodule.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Cacheable(value = "userByEmail", key = "#email")
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        System.out.println("Запрос в базу!");
        return userMapper.toDto(
                userRepository.findByEmailIgnoreCase(email)
                        .orElseThrow(() -> new EntityNotFoundException("Пользователь не был найден")));
    }
}
