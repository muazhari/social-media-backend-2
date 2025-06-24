package com.muazhari.socialmediabackend2.outers.repositories.threes;

import com.muazhari.socialmediabackend2.inners.models.entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findAllByChatRoomId(UUID chatRoomId);

    List<ChatMessage> findAllByAccountIdIn(List<UUID> accountIds);

    List<ChatMessage> findAllByIdIn(List<UUID> chatMessageIds);

    List<ChatMessage> findAllByChatRoomIdIn(List<UUID> chatRoomIds);
}
