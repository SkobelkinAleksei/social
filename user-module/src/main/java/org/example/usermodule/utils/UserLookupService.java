package org.example.usermodule.utils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.usermodule.entity.enums.UserEntity;
import org.example.usermodule.repository.UserRepository;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class UserLookupService {
    private final UserRepository userRepository;

    public UserEntity getById(Long userId) {
        if (isNull(userId)) {
            throw new NullPointerException("UserId: " + userId + " не может быть null!");
        }

        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь не был найден.")
        );
    }
}