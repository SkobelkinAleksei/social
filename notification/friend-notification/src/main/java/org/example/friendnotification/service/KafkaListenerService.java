package org.example.friendnotification.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.common.dto.friend.FriendNotificationRequestDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaListenerService {

    @KafkaListener(
            topics = "friend-notification-request",
            groupId = "friend-notification",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(
            @Payload ConsumerRecord<String, FriendNotificationRequestDto> consumer
    ) {
        FriendNotificationRequestDto data = consumer.value();
        log.info("Received message: {}", data);
        consumer.headers().forEach(header -> log.info("Header: {}", header));
    }
}
