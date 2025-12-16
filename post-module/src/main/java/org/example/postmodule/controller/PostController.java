package org.example.postmodule.controller;

import lombok.RequiredArgsConstructor;
import org.example.postmodule.dto.NewPostDto;
import org.example.postmodule.dto.PostDto;
import org.example.postmodule.dto.UpdatePostDto;
import org.example.postmodule.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RequiredArgsConstructor
@RequestMapping("/social/v1/posts")
@RestController
public class PostController {
    private final PostService postService;

    @PostMapping("/create-post")
    public ResponseEntity<PostDto> createPost(
            @RequestBody NewPostDto newPostDto,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return ResponseEntity.ok().body(postService.createPost(newPostDto, userId));
    }

    @PutMapping("/{postId}/update-post")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long postId,
            @RequestBody UpdatePostDto updatePostDto,
            @RequestHeader("X-User-id") Long userId
    ) throws AccessDeniedException {
        return ResponseEntity.ok().body(postService.updatePost(postId, updatePostDto, userId));
    }
}
