package org.example.postmodule.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.security.SecurityUtil;
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
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostByIdForUser(
            @PathVariable(name = "postId") Long postId
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        log.info("[INFO] Пришел запрос от пользователя с id: {} на получение поста с id: {}", currentUserId, postId);
        return ResponseEntity.ok().body(postService.getPostByIdForUser(currentUserId, postId));
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
    @GetMapping("/my-posts")
    public ResponseEntity<List<PostDto>> findUserPostsByStatus(
            @RequestParam(required = false) List<ModerationStatusPost> moderationStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        log.info("[INFO] Пришел запрос на поиск постов автора id: {} по статусам: {}, страница: {}, размер: {}",
                currentUserId, moderationStatus, page, size);
        return ResponseEntity.ok().body(postService.findUserPostsByStatus(currentUserId, moderationStatus, page, size));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/submit")
    public ResponseEntity<Long> submitPost(
            @RequestBody NewPostDto newPostDto
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        log.info("[INFO] Пришел запрос на создание нового поста от автора с id: {}", currentUserId);
        return ResponseEntity.ok().body(
                postService.submitPost(newPostDto, currentUserId)
        );
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{postId}/update-post")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long postId,
            @RequestBody UpdatePostDto updatePostDto
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        log.info("[INFO] Пришел запрос на обновление поста с id: {} пользователем с id: {}", postId, currentUserId);
        return ResponseEntity.ok().body(postService.updatePost(postId, updatePostDto, currentUserId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable(name = "postId") Long postId,
            HttpServletRequest request
    ) {
//        postService.deletePost(postId, userId);
//        return ResponseEntity.noContent().build();
        Long currentUserId = SecurityUtil.getCurrentUserId();
        log.info("[INFO] Пришел запрос удаление поста с id: {} пользователем с id: {}", postId, currentUserId);
        return ResponseEntity.ok().body(postService.deletePost(postId, currentUserId));
    }
}
