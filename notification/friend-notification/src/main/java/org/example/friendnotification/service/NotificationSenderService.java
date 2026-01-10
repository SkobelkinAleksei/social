//package org.example.friendnotification.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.example.common.NotificationBroadcaster;
//import org.example.common.dto.friend.FriendNotificationDto;
//import org.example.common.dto.friend.FriendNotificationResponseDto;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class KafkaListenerService {
//
//    private final NotificationBroadcaster broadcaster; // ✅ Правильный тип
//
//    @KafkaListener(topics = "${kafka.topics.friend-notification-request.name}",
//            containerFactory = "requestListenerFactory")
//    public void listenRequest(FriendNotificationDto dto) {
//        log.info("[KAFKA→NOTIFY] Запрос дружбы для userId={}: {}", dto.userId(), dto);
//
//        // ✅ Создаем событие и рассылаем
//        NotificationBroadcaster.NotificationEvent event = new NotificationBroadcaster.NotificationEvent();
//        event.setType("friend_request");
//        event.setUserId(dto.userId()); // кому показывать уведомление
//        event.setMessage("Пользователь ID " + dto.friendId() + " отправил запрос дружбы");
//        broadcaster.broadcast(event);
//    }
//
//    @KafkaListener(topics = "${kafka.topics.friend-notification-response.name}",
//            containerFactory = "responseListenerFactory")
//    public void handleResponse(FriendNotificationResponseDto dto) {
//        log.info("[KAFKA→NOTIFY] Ответ на заявку: {}", dto);
//
//        NotificationBroadcaster.NotificationEvent event = new NotificationBroadcaster.NotificationEvent();
//        event.setType("friend_response");
//        event.setUserId(dto.userId()); // TODO: проверить структуру DTO
//        event.setMessage(dto.status().equals("ACCEPTED") ? "✅ Заявка принята!" : "❌ Заявка отклонена");
//        broadcaster.broadcast(event);
//    }
//
//    @KafkaListener(topics = "${kafka.topics.friend-notification-delete.name}",
//            containerFactory = "deleteListenerFactory")
//    public void handleDelete(FriendNotificationDto dto) {
//        log.info("[KAFKA→NOTIFY] Удаление друга: {}", dto);
//
//        NotificationBroadcaster.NotificationEvent event = new NotificationBroadcaster.NotificationEvent();
//        event.setType("friend_delete");
//        event.setUserId(dto.userId());
//        event.setMessage("👋 Друг ID " + dto.friendId() + " вас удалил");
//        broadcaster.broadcast(event);
//    }
//}
package org.example.friendnotification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.friend.FriendNotificationDto;
import org.example.common.dto.friend.FriendNotificationResponseDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSenderService {

    private final RestTemplate restTemplate = new RestTemplate();

    @KafkaListener(topics = "${kafka.topics.friend-notification-request.name}",
            containerFactory = "requestListenerFactory")
    public void sendFriendRequest(FriendNotificationDto dto) {
        log.info("[KAFKA→LIVECHAT] Friend request: {}", dto);
        restTemplate.postForEntity("http://localhost:8070/api/notify/friend-request", dto, Void.class);
    }

    @KafkaListener(topics = "${kafka.topics.friend-notification-response.name}",
            containerFactory = "responseListenerFactory")
    public void sendFriendResponse(FriendNotificationResponseDto dto) {
        log.info("[KAFKA→LIVECHAT] Friend response: {}", dto);
        restTemplate.postForEntity("http://localhost:8070/api/notify/friend-response", dto, Void.class);
    }

    @KafkaListener(topics = "${kafka.topics.friend-notification-delete.name}",
            containerFactory = "deleteListenerFactory")
    public void sendFriendDelete(FriendNotificationDto dto) {
        log.info("[KAFKA→LIVECHAT] Friend delete: {}", dto);
        restTemplate.postForEntity("http://localhost:8070/api/notify/friend-delete", dto, Void.class);
    }
}
