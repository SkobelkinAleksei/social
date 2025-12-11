package org.example.postmodule.controller;

import lombok.RequiredArgsConstructor;
import org.example.postmodule.dto.NewPostDto;
import org.example.postmodule.dto.PostDto;
import org.example.postmodule.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/social/v1/posts")
@RestController
public class PostController {
    private final PostService postService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create-post")
    public ResponseEntity<PostDto> createPost(@RequestBody NewPostDto newPostDto) {
        return ResponseEntity.ok().body(postService.createPost(newPostDto));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/update-post")
    public ResponseEntity<PostDto> updatePost(NewPostDto newPostDto) {
        return ResponseEntity.ok().body(postService.createPost(newPostDto));
    }
}
