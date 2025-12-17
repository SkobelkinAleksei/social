package org.example.usermodule.service;

import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.LoginUserDto;
import org.example.usermodule.dto.authDto.RegistrationUserDto;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.entity.UserEntity;
import org.example.usermodule.entity.enums.Role;
import org.example.usermodule.mapper.UserMapper;
import org.example.usermodule.repository.UserRepository;
import org.example.usermodule.security.JwtResponse;
import org.example.usermodule.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserDto createUserAccount(RegistrationUserDto registrationUserDto) {
        UserEntity userEntity = userMapper.toEntity(registrationUserDto);

        userRepository.findByEmailIgnoreCase(userEntity.getEmail())
                .ifPresent(e -> {
                    throw new IllegalArgumentException("Такой email уже используется");
                });

        userRepository.findByNumberPhone(userEntity.getNumberPhone())
                .ifPresent(e -> {
                    throw new IllegalArgumentException("Такой номер телефона уже используется");
                });

        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setRole(Role.USER);
        userEntity.setTimeStamp(LocalDateTime.now());

        return userMapper.toDto(
                userRepository.save(userEntity)
        );
    }

    @Transactional(readOnly = true)
    public JwtResponse login(LoginUserDto dto) {

        UserEntity user = userRepository.findByEmailIgnoreCase(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Неверный email или password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Неверный email или password");
        }

        return new JwtResponse(
                jwtUtil.generateRefreshToken(user)
        );
    }

    @Transactional(readOnly = true)
    public JwtResponse refresh(String refreshToken) {
        Claims claims = jwtUtil.validateRefreshToken(refreshToken);

        Long userId = claims.get("id", Long.class);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        return new JwtResponse(
                jwtUtil.generateRefreshToken(user)
        );
    }
}
