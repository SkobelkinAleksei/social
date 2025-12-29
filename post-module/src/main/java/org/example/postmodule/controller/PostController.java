package org.example.postmodule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postmodule.dto.NewPostDto;
import org.example.postmodule.dto.PostDto;
import org.example.postmodule.dto.UpdatePostDto;
import org.example.postmodule.entity.ModerationStatusPost;
import org.example.postmodule.service.UserPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/social/posts")
@RestController
public class PostController {
    private final UserPostService postService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{userId}/{postId}")
    public ResponseEntity<PostDto> getPostByIdForUser(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "postId") Long postId
    ) {
        log.info("[INFO] Пришел запрос от пользователя с id: {} на получение поста с id: {}", userId, postId);
        return ResponseEntity.ok().body(postService.getPostByIdForUser(userId, postId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/id/{postId}")
    public ResponseEntity<PostDto> getPostById(
            @PathVariable(name = "postId") Long postId
    ) {
        log.info("[INFO] Пришел запрос на получение поста с id: {}", postId);
        return ResponseEntity.ok().body(postService.getPostById(postId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getUserPosts(
            @PathVariable(name = "userId") Long userId
    ) {
        log.info("[INFO] Пришел запрос на получение всех постов пользователя с id: {}", userId);
        return ResponseEntity.ok().body(postService.getUserPosts(userId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{authorId}")
    public ResponseEntity<List<PostDto>> findUserPostsByStatus(
            @PathVariable(name = "authorId") Long authorId,
            @RequestParam(required = false) List<ModerationStatusPost> moderationStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        log.info("[INFO] Пришел запрос на поиск постов автора id: {} по статусам: {}, страница: {}, размер: {}",
                authorId, moderationStatus, page, size);
        return ResponseEntity.ok().body(postService.findUserPostsByStatus(authorId, moderationStatus, page, size));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/{userId}")
    public ResponseEntity<Long> submitPost(
            @RequestBody NewPostDto newPostDto,
            @PathVariable(name = "userId") Long userId
    ) {
        log.info("[INFO] Пришел запрос на создание нового поста от автора с id: {}", userId);
        return ResponseEntity.ok().body(
                postService.submitPost(newPostDto, userId)
        );
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{userId}/{postId}/update-post")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long postId,
            @RequestBody UpdatePostDto updatePostDto,
            @PathVariable(name = "userId") Long userId
    ) {
        log.info("[INFO] Пришел запрос на обновление поста с id: {} пользователем с id: {}", postId, userId);
        return ResponseEntity.ok().body(postService.updatePost(postId, updatePostDto, userId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{userId}/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable(name = "postId") Long postId,
            @PathVariable(name = "userId") Long userId
    ) {
//        postService.deletePost(postId, userId);
//        return ResponseEntity.noContent().build();
        log.info("[INFO] Пришел запрос удаление поста с id: {} пользователем с id: {}", postId, userId);
        return ResponseEntity.ok().body(postService.deletePost(postId, userId));
    }
}
