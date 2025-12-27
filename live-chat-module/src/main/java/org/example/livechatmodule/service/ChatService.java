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
    public ChatEntity getOrCreatedPrivateChat(
            Long userIdOne,
            Long userIdTwo
    ) {
        return chatRepository.findPrivateChat(userIdOne, userIdTwo)
                .orElseGet(() -> {
                    ChatEntity chatEntity = new ChatEntity();
                    chatEntity.setChatType(ChatType.PRIVATE);

                    ChatParticipant chatParticipantOne = new ChatParticipant();
                    chatParticipantOne.setUserId(userIdOne);
                    chatParticipantOne.setChat(chatEntity);

                    ChatParticipant chatParticipantTwo = new ChatParticipant();
                    chatParticipantTwo.setUserId(userIdTwo);
                    chatParticipantTwo.setChat(chatEntity);

                    chatEntity.getParticipantSet().addAll(
                            Set.of(chatParticipantOne, chatParticipantTwo)
                    );

                    return chatEntity;
                });
    }
}
