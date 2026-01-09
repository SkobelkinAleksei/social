package org.example.livechatmodule.repository;

import org.example.livechatmodule.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    @Query("""
        SELECT ce
        FROM ChatEntity ce
        JOIN ce.participantSet p1
        JOIN ce.participantSet p2
        WHERE ce.chatType= 'PRIVATE'
        AND p1.userId= :userIdOne
        AND p2.userId= :userIdTwo
    """)
    Optional<ChatEntity> findPrivateChat(Long userIdOne, Long userIdTwo);
}