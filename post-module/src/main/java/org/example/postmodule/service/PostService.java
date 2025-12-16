package org.example.postmodule.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.postmodule.dto.NewPostDto;
import org.example.postmodule.dto.PostDto;
import org.example.postmodule.dto.UpdatePostDto;
import org.example.postmodule.entity.PostEntity;
import org.example.postmodule.mapper.PostMapper;
import org.example.postmodule.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Transactional
    public PostDto createPost(NewPostDto newPostDto, Long authorId) {
        PostEntity postEntity = postMapper.toEntity(newPostDto);

        postEntity.setAuthorId(authorId);
        postEntity.setCreatedAt(LocalDateTime.now());

        postRepository.save(postEntity);

        return postMapper.toDto(postEntity);
    }

    @Transactional
    public PostDto updatePost(Long postId, UpdatePostDto updatePostDto, Long userId) throws AccessDeniedException {
        PostEntity postEntity = postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Такой пост не был найден.")
        );

        if (!postEntity.getAuthorId().equals(userId)) {
            throw new AccessDeniedException("Пользователь не является автором поста!");
        }

        postEntity.setContent(updatePostDto.getContent());
        postEntity.setUpdatedAt(LocalDateTime.now());

        return postMapper.toDto(postEntity);
    }
}
