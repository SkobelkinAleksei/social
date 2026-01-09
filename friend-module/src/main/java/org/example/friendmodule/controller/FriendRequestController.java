package org.example.friendmodule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.security.SecurityUtil;
import org.example.friendmodule.dto.FriendRequestDto;
import org.example.friendmodule.entity.FriendRequestStatus;
import org.example.friendmodule.entity.ResponseFriendRequest;
import org.example.friendmodule.service.FriendRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/social/friends/requests")
@RestController
public class FriendRequestController {
    private final FriendRequestService friendRequestService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<FriendRequestDto> addRequestFriend(
            @RequestParam Long addresseeId
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok().body(friendRequestService.addRequestFriend(currentUserId, addresseeId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> deleteRequestFriend(
            @RequestParam Long addresseeId
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        friendRequestService.deleteRequestFriend(currentUserId, addresseeId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/outgoing")
    public ResponseEntity<List<FriendRequestDto>> getRequesterRequestSpecification(
            @RequestParam(required = false) FriendRequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok().body(
                friendRequestService.getRequesterRequestSpecification(currentUserId, status, page, size)
        );
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/incoming")
    public ResponseEntity<List<FriendRequestDto>> getUserRequestsFromFriends(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok().body(
                friendRequestService.getUserRequestsFromFriends(currentUserId, page, size)
        );
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{requestId}")
    public ResponseEntity<String> responseToRequest(
            @PathVariable Long requestId,
            @RequestParam ResponseFriendRequest status
    ) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok().body(
                friendRequestService.responseToRequest(requestId, status, currentUserId)
        );
    }
}
