package com.muazhari.socialmediabackend2.inners.models.entities;

import com.muazhari.socialmediabackend2.inners.models.Model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "chat_room")
@Entity
public class ChatRoom extends Model {
    @Id
    UUID id;
    String name;
    String description;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY)
    Set<ChatMessage> chatMessages;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY)
    Set<ChatRoomMember> chatRoomMembers;
}