package com.muazhari.socialmediabackend2.outers.repositories.threes;

import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    List<ChatRoom> findAllByIdIn(List<UUID> chatRoomIds);
}
