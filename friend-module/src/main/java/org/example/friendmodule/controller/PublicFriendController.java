package org.example.friendmodule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.friendmodule.dto.FriendDto;
import org.example.friendmodule.service.FriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/social/friends/public")
@RestController
public class PublicFriendController {
    private final FriendService friendService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<List<FriendDto>> findAllFriendByUserId(
            @PathVariable(name = "userId") Long userId
    ) {
        return ResponseEntity.ok().body(friendService.findAllFriendByUserId(userId));
    }
}
