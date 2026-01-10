package org.example.livechatmodule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.NotificationBroadcaster;
import org.example.common.NotificationBroadcaster.NotificationEvent;
import org.example.common.dto.friend.FriendNotificationDto;
import org.example.common.dto.friend.FriendNotificationResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationBroadcaster broadcaster;

    @PostMapping("/api/notify/friend-request")
    public ResponseEntity<String> friendRequest(@RequestBody FriendNotificationDto dto) {
        log.info("🔔 Запрос в друзья: От {} → {}", dto.friendId(), dto.userId());
        String message = "🔔 Пользователь " + dto.friendId() + " отправил запрос в друзья";
        broadcaster.broadcast(new NotificationEvent("request", dto.userId(), message));
        log.debug("Уведомление о запросе отправлено пользователю {}", dto.userId());
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/api/notify/friend-response")
    public ResponseEntity<String> friendResponse(@RequestBody FriendNotificationResponseDto dto) {
        log.info("🔔 Ответ на заявку {} для пользователя {}", dto.status(), dto.userId());
        String message = "ACCEPTED".equals(dto.status()) ? "✅ Заявка принята!" : "❌ Заявка отклонена";
        broadcaster.broadcast(new NotificationEvent("response", dto.userId(), message));
        log.debug("Уведомление об ответе отправлено пользователю {}", dto.userId());
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/api/notify/friend-delete")
    public ResponseEntity<String> friendDelete(@RequestBody FriendNotificationDto dto) {
        log.info("🔔 Удаление из друзей: {} удалил {}", dto.friendId(), dto.userId());
        String message = "👋 Друг " + dto.friendId() + " удалил вас";
        broadcaster.broadcast(new NotificationEvent("delete", dto.userId(), message));
        log.debug("Уведомление об удалении отправлено пользователю {}", dto.userId());
        return ResponseEntity.ok("OK");
    }
}
