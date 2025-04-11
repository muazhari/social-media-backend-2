package com.muazhari.socialmediabackend2.outers.deliveries.controllers;

import com.muazhari.socialmediabackend2.inners.models.entities.ChatMessage;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoom;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoomMember;
import com.muazhari.socialmediabackend2.inners.usecases.ChatUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @BatchMapping(typeName = "ChatMessage", field = "room")
    public Map<ChatMessage, ChatRoom> chatMessageRoom(List<ChatMessage> chatMessages) {
        Map<UUID, ChatMessage> chatMessageMaps = chatMessages
                .stream()
                .collect(Collectors.toMap(ChatMessage::getId, chatMessage -> chatMessage, (a, b) -> a));

        List<ChatRoom> foundChatRooms = chatUseCase.getChatRoomsByIds(
                chatMessageMaps.keySet().stream().toList()
        );

        return foundChatRooms
                .stream()
                .collect(Collectors.toMap(
                        chatRoom -> chatMessageMaps.get(chatRoom.getId()),
                        chatRoom -> chatRoom
                ));
    }

    @BatchMapping(typeName = "ChatRoomMember", field = "room")
    public Map<ChatRoomMember, ChatRoom> chatRoomMemberRoom(List<ChatRoomMember> chatRoomMembers) {
        Map<UUID, ChatRoomMember> chatRoomMemberMaps = chatRoomMembers
                .stream()
                .collect(Collectors.toMap(ChatRoomMember::getId, chatRoomMember -> chatRoomMember, (a, b) -> a));

        List<ChatRoomMember> foundChatRoomMembers = chatUseCase.getChatRoomMembersByIds(
                chatRoomMemberMaps.keySet().stream().toList()
        );

        return foundChatRoomMembers
                .stream()
                .collect(Collectors.toMap(
                        chatRoomMember -> chatRoomMemberMaps.get(chatRoomMember.getId()),
                        ChatRoomMember::getChatRoom
                ));
    }

    @BatchMapping
    public Map<ChatRoom, List<ChatRoomMember>> members(List<ChatRoom> chatRooms) {
        Map<UUID, ChatRoom> chatRoomMaps = chatRooms
                .stream()
                .collect(Collectors.toMap(ChatRoom::getId, chatRoom -> chatRoom, (a, b) -> a));

        List<ChatRoomMember> foundChatRoomMembers = chatUseCase.getChatRoomMembersByChatRoomIds(
                chatRoomMaps.keySet().stream().toList()
        );

        return foundChatRoomMembers
                .stream()
                .collect(Collectors.groupingBy(
                        chatRoomMember -> chatRoomMaps.get(chatRoomMember.getChatRoom().getId()),
                        Collectors.mapping(chatRoomMember -> chatRoomMember, Collectors.toList())
                ));
    }

    @BatchMapping
    public Map<ChatRoom, List<ChatMessage>> messages(List<ChatRoom> chatRooms) {
        Map<UUID, ChatRoom> chatRoomMaps = chatRooms
                .stream()
                .collect(Collectors.toMap(ChatRoom::getId, chatRoom -> chatRoom, (a, b) -> a));

        List<ChatMessage> foundChatMessages = chatUseCase.getChatMessagesByIds(
                chatRoomMaps.keySet().stream().toList()
        );

        return foundChatMessages
                .stream()
                .collect(Collectors.groupingBy(
                        chatMessage -> chatRoomMaps.get(chatMessage.getChatRoom().getId()),
                        Collectors.mapping(chatMessage -> chatMessage, Collectors.toList())
                ));
    }

}
