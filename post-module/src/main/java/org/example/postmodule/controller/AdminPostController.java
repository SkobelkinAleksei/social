package org.example.postmodule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.postmodule.dto.PostDto;
import org.example.postmodule.dto.PostFullDto;
import org.example.postmodule.entity.ModerationStatusPost;
import org.example.postmodule.service.AdminPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/social/admin/posts")
@RestController
public class AdminPostController {
    private final AdminPostService adminService;

    @GetMapping("/{postId}")
    public ResponseEntity<PostFullDto> getPostById(
            @PathVariable(name = "postId") Long postId
    ) {
        log.info("[INFO] Пришел запрос на получение поста по id: {}", postId);
        return ResponseEntity.ok().body(adminService.getPostById(postId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PostDto>> getAllPendingPost(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("[INFO] Пришел запрос от ADMIN на получение всех постов со статусом PENDING, страница: {}, размер: {}", page, size);
        return ResponseEntity.ok().body(adminService.getAllPendingPost(page, size));
    }

    @GetMapping("/{authorId}/allPost")
    public ResponseEntity<List<PostDto>> findUserPostsByStatus(
            @PathVariable(name = "authorId") Long authorId,
            @RequestParam(required = false) List<ModerationStatusPost> moderationStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("[INFO] Пришел запрос от ADMIN на получение постов автора id: {} по статусам: {}, страница: {}, размер: {}",
                authorId, moderationStatus, page, size);
        return ResponseEntity.ok().body(adminService.findUserPostsByStatus(authorId, moderationStatus, page, size));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostDto> updateStatusPost(
            @PathVariable(name = "postId") Long postId,
            @RequestParam(required = true) ModerationStatusPost moderationStatus
    ) {
        log.info("[INFO] Пришел запрос от ADMIN на обновление статуса поста id: {} на {}", postId, moderationStatus);
        return ResponseEntity.ok().body(adminService.updateStatusPost(postId, moderationStatus));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePostById(
            @PathVariable(name = "postId") Long postId
    ) {
        log.info("[INFO] Пришел запрос от ADMIN на удаление поста по id: {}", postId);
        adminService.deletePostId(postId);
        return ResponseEntity.noContent().build();
    }

}
