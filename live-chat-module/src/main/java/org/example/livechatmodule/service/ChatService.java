package org.example.livechatmodule.service;

import lombok.RequiredArgsConstructor;
import org.example.livechatmodule.entity.ChatEntity;
import org.example.livechatmodule.entity.ChatParticipant;
import org.example.livechatmodule.entity.ChatType;
import org.example.livechatmodule.entity.MessageEntity;
import org.example.livechatmodule.repository.ChatRepository;
import org.example.livechatmodule.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public List<MessageEntity> loadMessages(
            Long chatId
    ) {
        List<MessageEntity> allByChatId = messageRepository.findAllByChat_Id(chatId);

        if (allByChatId.isEmpty()) {
            return Collections.emptyList();
        }

        return allByChatId;
    }

    @Transactional
    public ChatEntity getOrCreatedPrivateChat(Long userIdOne, Long userIdTwo) {
        return chatRepository.findPrivateChat(userIdOne, userIdTwo)
                .orElseGet(() -> {
                    ChatEntity chatEntity = new ChatEntity();
                    chatEntity.setChatType(ChatType.PRIVATE);

                    ChatParticipant participant1 = new ChatParticipant();
                    participant1.setUserId(userIdOne);
                    participant1.setChat(chatEntity);

                    ChatParticipant participant2 = new ChatParticipant();
                    participant2.setUserId(userIdTwo);
                    participant2.setChat(chatEntity);

                    chatEntity.getParticipantSet().addAll(Set.of(participant1, participant2));

                    return chatRepository.save(chatEntity);
                });
    }

    @Transactional(readOnly = true)
    public ChatEntity getChatById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found: " + chatId));
    }
}
