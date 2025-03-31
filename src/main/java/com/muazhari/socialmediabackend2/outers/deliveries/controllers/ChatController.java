package com.muazhari.socialmediabackend2.outers.deliveries.controllers;

import com.muazhari.socialmediabackend2.inners.models.entities.ChatMessage;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoom;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoomMember;
import com.muazhari.socialmediabackend2.inners.usecases.ChatUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.federation.EntityMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class ChatController {

    @Autowired
    ChatUseCase chatUseCase;

    @QueryMapping
    public List<ChatRoom> chatRooms() {
        return chatUseCase.getChatRooms();
    }

    @QueryMapping
    public List<ChatRoomMember> chatRoomMembers(@Argument UUID chatRoomId) {
        return chatUseCase.getChatRoomMembers(chatRoomId);
    }

    @QueryMapping
    public List<ChatMessage> chatMessages(@Argument UUID chatRoomId) {
        return chatUseCase.getChatMessages(chatRoomId);
    }

    @MutationMapping
    public ChatRoom addChatRoom(@Argument String name, @Argument String description) {
        return chatUseCase.addChatRoom(name, description);
    }

    @MutationMapping
    public ChatRoomMember addMemberToChatRoom(@Argument UUID accountId, @Argument UUID chatRoomId) {
        return chatUseCase.addMemberToChatRoom(accountId, chatRoomId);
    }

    @MutationMapping
    public ChatMessage addChatMessage(@Argument UUID chatRoomId, @Argument UUID accountId, @Argument String content) {
        return chatUseCase.addChatMessage(chatRoomId, accountId, content);
    }

    @SchemaMapping
    public ChatRoom room(ChatMessage chatMessage) {
        return chatUseCase.getChatRoomByMessageId(chatMessage.getId());
    }

    @SchemaMapping
    public ChatRoom room(ChatRoomMember chatRoomMember) {
        return chatUseCase.getChatRoomByMemberId(chatRoomMember.getId());
    }

    @SchemaMapping
    public List<ChatRoomMember> members(ChatRoom chatRoom) {
        return chatUseCase.getChatRoomMembers(chatRoom.getId());
    }

    @SchemaMapping
    public List<ChatMessage> messages(ChatRoom chatRoom) {
        return chatUseCase.getChatMessages(chatRoom.getId());
    }

    @EntityMapping
    public List<ChatRoom> chatRoom(@Argument List<UUID> idList) {
        return chatUseCase.getChatRoomsByIds(idList);
    }

    @EntityMapping
    public List<ChatRoomMember> chatRoomMember(@Argument List<UUID> idList) {
        return chatUseCase.getChatRoomMembersByIds(idList);
    }

    @EntityMapping
    public List<ChatMessage> chatMessage(@Argument List<UUID> idList) {
        return chatUseCase.getChatMessagesByIds(idList);
    }

}
