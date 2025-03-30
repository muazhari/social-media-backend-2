package com.muazhari.socialmediabackend2.outers.repositories.threes;

import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, UUID> {
    List<ChatRoomMember> findAllByChatRoomId(UUID chatRoomId);

    List<ChatRoomMember> findAllByAccountIdIn(List<UUID> accountIds);

    List<ChatRoomMember> findAllByIdIn(List<UUID> chatRoomMemberIds);
}
