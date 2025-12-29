package org.example.friendmodule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.friendmodule.service.PrivateFriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/social/friends/private")
@RestController
public class PrivateFriendController {
    private final PrivateFriendService privateFriendService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> deleteFriendById(
            @RequestParam Long userId1,
            @RequestParam Long userId2
    ) {
        privateFriendService.deleteFriendById(userId1, userId2);
        return ResponseEntity.noContent().build();
    }
}
