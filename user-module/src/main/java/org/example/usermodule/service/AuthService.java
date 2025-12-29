package org.example.usermodule.service;

import lombok.RequiredArgsConstructor;
import org.example.usermodule.dto.authDto.LoginResponse;
import org.example.usermodule.entity.enums.UserEntity;
import org.example.usermodule.entity.enums.enums.Role;
import org.example.usermodule.dto.authDto.LoginUserDto;
import org.example.usermodule.dto.authDto.RegistrationUserDto;
import org.example.usermodule.dto.UserDto;
import org.example.usermodule.mapper.UserMapper;
import org.example.usermodule.repository.UserRepository;
import org.example.usermodule.security.AuthUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;

    @Transactional
    public UserDto signUp(RegistrationUserDto registrationUserDto) {
        boolean existByEmailOrNumberPhone = userRepository.isExistByEmailOrNumberPhone(
                registrationUserDto.getEmail(),
                registrationUserDto.getNumberPhone()
        );

        if (existByEmailOrNumberPhone) {
            throw new IllegalArgumentException("Email или телефон уже используются!");
        }

        UserEntity userEntity = userMapper.toEntity(registrationUserDto);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setRole(Role.USER);
        userEntity.setTimeStamp(LocalDateTime.now());

        return userMapper.toDto(
                userRepository.save(userEntity)
        );
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginUserDto dto) throws AccessDeniedException {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        UserEntity user = (UserEntity) authenticate.getPrincipal();

        if (!user.isEnabled()) {
            throw new AccessDeniedException("Аккаунт недоступен!");
        }
        String token = authUtil.generateAccessToken(user);
        return new LoginResponse(token, user.getId());
    }
}
