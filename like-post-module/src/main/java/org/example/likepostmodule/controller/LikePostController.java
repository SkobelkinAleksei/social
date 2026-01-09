package org.example.likepostmodule.controller;

import lombok.RequiredArgsConstructor;
import org.example.common.security.SecurityUtil;
import org.example.likepostmodule.dto.LikePostDto;
import org.example.likepostmodule.service.LikePostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/social/likes")
@RestController
public class LikePostController {
    private final LikePostService likeService;

    @GetMapping("/{postId}/list-like")
    public ResponseEntity<List<LikePostDto>> getLikesByPostId(
            @PathVariable(name = "postId") Long postId
    ) {
        return ResponseEntity.ok().body(likeService.getLikesByPostId(postId));
    }

    @GetMapping("/{postId}/is-liked")
    public ResponseEntity<Boolean> isLikedByUser(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        boolean isLiked = likeService.isLikedByUser(postId, userId);
        return ResponseEntity.ok(isLiked);
    }

    @GetMapping("/{postId}/likes-count")
    public ResponseEntity<Long> getLikesCount(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.countActiveLikesByPostId(postId));
    }

    @PostMapping("/{postId}")
    public ResponseEntity<String> toggleLike(
        @PathVariable(name = "postId") Long postId
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok().body(likeService.toggleLike(postId, currentUserId));
    }
}
