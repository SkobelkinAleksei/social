package org.example.postmodule.service;

import lombok.AllArgsConstructor;
import org.example.postmodule.dto.NewPostDto;
import org.example.postmodule.dto.PostDto;
import org.example.postmodule.entity.PostEntity;
import org.example.postmodule.mapper.PostMapper;
import org.example.postmodule.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Transactional
    public PostDto createPost(NewPostDto newPostDto, Long userId) {
        PostEntity postEntity = postMapper.toEntity(newPostDto);

        postEntity.setUserId(userId);
        postRepository.save(postEntity);

        return postMapper.toDto(postEntity);
    }
}
