package org.example.postmodule.util;

import org.example.postmodule.entity.PostEntity;
import org.springframework.stereotype.Service;

@Service
public class PostAccessValidator {

    public void validateAuthor(PostEntity postEntity, Long userId) {
        if (!postEntity.getAuthorId().equals(userId)) {
            throw new IllegalArgumentException("Пользователь не является автором поста!");
        }
    }
}
