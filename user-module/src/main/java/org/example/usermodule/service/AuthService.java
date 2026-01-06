package org.example.usermodule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.auth.LoginResponse;
import org.example.common.dto.auth.LoginUserDto;
import org.example.common.dto.auth.RegistrationUserDto;
import org.example.common.dto.TokenGenerationRequest;
import org.example.security.service.JwtTokenProvider;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.entity.enums.UserEntity;
import org.example.usermodule.entity.enums.enums.Role;
import org.example.usermodule.mapper.UserMapper;
import org.example.usermodule.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto signUp(RegistrationUserDto registrationUserDto) {
        log.info("[INFO] Создание пользователя с email: {} и номером телефона: {}",
                registrationUserDto.getEmail(), registrationUserDto.getNumberPhone());

        boolean existByEmailOrNumberPhone = userRepository.isExistByEmailOrNumberPhone(
                registrationUserDto.getEmail(),
                registrationUserDto.getNumberPhone()
        );

        if (existByEmailOrNumberPhone) {
            log.warn("[INFO] Попытка создания пользователя с уже существующими email или телефоном: email={}, phone={}",
                    registrationUserDto.getEmail(), registrationUserDto.getNumberPhone());

            throw new IllegalArgumentException("Email или телефон уже используются!");
        }

        UserEntity userEntity = userMapper.toEntity(registrationUserDto);
        userEntity.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        userEntity.setRole(Role.USER);
        userEntity.setTimeStamp(LocalDateTime.now());

        UserDto userDto = userMapper.toDto(userRepository.save(userEntity));

        log.info("[INFO] Пользователь успешно создан с id: {}", userDto.getUserId());
        return userDto;
    }

    public LoginResponse login(LoginUserDto dto) throws AccessDeniedException {
        log.info("[DEBUG] Попытка логина с email: '{}'", dto.getEmail());  // ← ДОБАВИТЬ

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
        log.info("[DEBUG] Аутентификация успешна. Username: '{}'", userDetails.getUsername());  // ← ДОБАВИТЬ

        UserEntity user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> {
                    log.error("[ERROR] Пользователь не найден для email: '{}'", userDetails.getUsername());  // ← ДОБАВИТЬ
                    return new UsernameNotFoundException("Пользователь не найден");
                });

        if (!user.isEnabled()) {
            throw new AccessDeniedException("Аккаунт недоступен!");
        }

        TokenGenerationRequest tokenRequest = new TokenGenerationRequest(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
        String token = jwtTokenProvider.generateAccessToken(tokenRequest);
        return new LoginResponse(token, user.getId());
    }
}
