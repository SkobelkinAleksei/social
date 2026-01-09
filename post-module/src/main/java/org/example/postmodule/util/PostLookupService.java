package org.example.postmodule.util;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postmodule.entity.PostEntity;
import org.example.postmodule.repository.PostRepository;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostLookupService {
    private final PostRepository postRepository;

    public PostEntity getById(Long postId) {
        if (isNull(postId)) {
            throw new NullPointerException("PostId: " + postId + " не может быть null!");
        }

        return postRepository.findById(postId).orElseThrow(
                () -> {
                    log.warn("[ERROR] Пост с id: {} не найден", postId);
                    return new EntityNotFoundException("Такой пост не был найден.");
                });
    }
}