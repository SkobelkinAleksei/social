package org.example.likepostmodule.controller;

import lombok.RequiredArgsConstructor;
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

    @PostMapping("/{postId}")
    public ResponseEntity<String> toggleLike(
        @PathVariable(name = "postId") Long postId,
        @RequestParam(name = "authorId") Long authorId
    ) {
        return ResponseEntity.ok().body(likeService.toggleLike(postId, authorId));
    }

}
