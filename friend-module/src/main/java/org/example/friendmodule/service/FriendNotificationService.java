package org.example.friendmodule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.friend.FriendNotificationDto;
import org.example.common.dto.friend.FriendNotificationResponseDto;
import org.example.kafka.kafkaConfig.KafkaValueConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendNotificationService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaValueConfig kafkaValueConfig;

    public void sendFriendRequestNotification(FriendNotificationDto friendNotificationDto) {
        try {
            String topic = kafkaValueConfig.getTopics().get("friend-notification-request").getName();
            log.info("[KAFKA-INFO] Получили запрос на отправку уведомления о запросе дружбы: {}", friendNotificationDto);
            kafkaTemplate.send(topic, friendNotificationDto);
            log.info("[KAFKA-INFO] Отправлено уведомление о запросе дружбы: {}", friendNotificationDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void responseToRequestNotification(
            FriendNotificationResponseDto dto
    ) {
        try {
            String topic = kafkaValueConfig.getTopics().get("friend-notification-response").getName();
            log.info("[KAFKA-INFO] Получили запрос на отправку уведомления об ответе: {}", dto);
            kafkaTemplate.send(topic, dto);
            log.info("[KAFKA-INFO] Уведомление о принятии/отклонении дружбы отправлено: {}", dto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFriendNotification(
            FriendNotificationDto dto
    ) {
        try {
            String topic = kafkaValueConfig.getTopics().get("friend-notification-delete").getName();
            log.info("[KAFKA-INFO] Получили запрос на отправку уведомления об прекращении дружбы: {}", dto);
            kafkaTemplate.send(topic, dto);
            log.info("[KAFKA-INFO] Уведомление о прекращении дружбы отправлено: {}", dto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
