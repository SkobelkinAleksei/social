package org.example.friendmodule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.common.dto.friend.FriendNotificationRequestDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendNotificationService {
    private final KafkaTemplate<String, FriendNotificationRequestDto> kafkaTemplate;

    public void sendFriendRequestNotification(FriendNotificationRequestDto friendNotificationRequestDto) {
        try {
            log.info("[KAFKA-INFO] Получили запрос на отправку уведомления о запросе дружбы: {}", friendNotificationRequestDto);
            ProducerRecord<String, FriendNotificationRequestDto> record = new ProducerRecord<>(
                    "friend-notification-request",
                    friendNotificationRequestDto
            );

            kafkaTemplate.send(record);
            log.info("[KAFKA-INFO] Отправлено уведомление о запросе дружбы: {}", friendNotificationRequestDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
