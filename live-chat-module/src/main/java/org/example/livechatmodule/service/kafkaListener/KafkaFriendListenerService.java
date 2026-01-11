package org.example.livechatmodule.service.kafkaListener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.common.NotificationBroadcaster;
import org.example.common.dto.friend.FriendNotificationDto;
import org.example.common.dto.friend.FriendNotificationResponseDto;
import org.example.common.dto.user.UserDto;
import org.example.livechatmodule.client.UserClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaFriendListenerService {
    private final NotificationBroadcaster broadcaster;
    private final ObjectMapper objectMapper;
    private final UserClient userClient;

    @KafkaListener(topics = "${kafka.topics.friend-notification-request.name}",
            groupId = "${kafka.groups.friend-request}")
    public void listenRequest(ConsumerRecord<String, String> record) {
        try {
            FriendNotificationDto dto = objectMapper.readValue(record.value(), FriendNotificationDto.class);
            String requesterName = getFullName(dto.friendId());
            broadcaster.broadcast(new NotificationBroadcaster.NotificationEvent(
                    "friend_request", dto.userId(),
                    "🔔 " + requesterName + " хочет добавить вас в друзья!"));
        } catch (Exception e) {
            log.error("[LIVECHAT-KAFKA-ERROR] Ошибка request: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topics.friend-notification-response.name}",
            groupId = "${kafka.groups.friend-response}")
    public void handleResponse(ConsumerRecord<String, String> record) {
        try {
            FriendNotificationResponseDto dto = objectMapper.readValue(record.value(), FriendNotificationResponseDto.class);

            String message = switch (dto.status()) {
                case "ACCEPTED" ->
                        "🔔 ✅ " + getFullName(dto.userId()) + " принял заявку! Теперь вы друзья";
                case "NOW_FRIENDS" ->
                        "🔔 👥 Вы приняли заявку от " + getFullName(dto.userId()) + "! Теперь друзья";
                case "REJECTED" ->
                        "🔔 ❌ В заявке в друзья к " + getFullName(dto.userId()) + " - отказано ";
                default -> dto.status();
            };

            broadcaster.broadcast(new NotificationBroadcaster.NotificationEvent(
                    "friend_response", dto.userId(), message));
        } catch (Exception e) {
            log.error("[LIVECHAT-KAFKA-ERROR] Ошибка response: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topics.friend-notification-delete.name}",
            groupId = "${kafka.groups.friend-delete}")
    public void handleDelete(ConsumerRecord<String, String> record) {
        try {
            FriendNotificationDto dto = objectMapper.readValue(record.value(), FriendNotificationDto.class);
            String deletedName = getFullName(dto.friendId());
            broadcaster.broadcast(new NotificationBroadcaster.NotificationEvent(
                    "friend_delete", dto.userId(),
                    "🔔 👋 " + deletedName + " вас удалил из друзей"));
        } catch (Exception e) {
            log.error("[LIVECHAT-KAFKA-ERROR] Ошибка delete: {}", e.getMessage());
        }
    }

    private String getFullName(Long userId) {
        try {
            UserDto user = userClient.getUserById(userId);
            if (user != null) {
                return user.getFirstName() + " " + user.getLastName();
            }
        } catch (Exception e) {
            log.debug("[DEBUG] getFullName fallback: {}", userId);
        }
        return "Пользователь ID " + userId;
    }
}
