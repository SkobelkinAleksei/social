package org.example.postmodule.controller;

import lombok.RequiredArgsConstructor;
import org.example.postmodule.dto.NewPostDto;
import org.example.postmodule.dto.PostDto;
import org.example.postmodule.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

//    @PreAuthorize("hasRole('USER')")
//    @PostMapping("/update-post")
//    public ResponseEntity<PostDto> updatePost(NewPostDto newPostDto) {
//        return ResponseEntity.ok().body(postService.createPost(newPostDto));
//    }
}
