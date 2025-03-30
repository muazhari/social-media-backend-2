package com.muazhari.socialmediabackend2.outers.repositories.twos;

import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<ChatRoom, UUID> {
}
