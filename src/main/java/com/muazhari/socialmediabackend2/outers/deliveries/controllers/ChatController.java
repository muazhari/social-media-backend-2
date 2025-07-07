package com.muazhari.socialmediabackend2.outers.deliveries.controllers;

import com.muazhari.socialmediabackend2.inners.models.entities.ChatMessage;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoom;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoomMember;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.ChatMessageInput;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.ChatRoomInput;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.ChatRoomMemberInput;
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
    public ChatRoom addChatRoom(@Argument ChatRoomInput input) {
        return chatUseCase.addChatRoom(input);
    }

    @MutationMapping
    public ChatRoomMember addMemberToChatRoom(@Argument ChatRoomMemberInput input) {
        return chatUseCase.addMemberToChatRoom(input);
    }

    @MutationMapping
    public ChatMessage addChatMessage(@Argument ChatMessageInput input) {
        return chatUseCase.addChatMessage(input);
    }

    @BatchMapping(typeName = "ChatMessage", field = "room")
    public Map<ChatMessage, ChatRoom> chatMessageRoom(List<ChatMessage> chatMessages) {
        List<ChatMessage> foundChatMessage = chatUseCase.getChatMessagesByIds(
                chatMessages.stream().map(ChatMessage::getId).toList()
        );

        return foundChatMessage
                .stream()
                .collect(Collectors.toMap(
                        chatMessage -> chatMessage,
                        ChatMessage::getChatRoom
                ));
    }

    @BatchMapping(typeName = "ChatRoomMember", field = "room")
    public Map<ChatRoomMember, ChatRoom> room(List<ChatRoomMember> chatRoomMembers) {
        List<ChatRoomMember> foundChatRoomMembers = chatUseCase.getChatRoomMembersByIds(
                chatRoomMembers.stream().map(ChatRoomMember::getId).toList()
        );

        return foundChatRoomMembers
                .stream()
                .collect(Collectors.toMap(
                        chatRoomMember -> chatRoomMember,
                        ChatRoomMember::getChatRoom
                ));
    }

    @BatchMapping
    public Map<ChatRoom, List<ChatRoomMember>> members(List<ChatRoom> chatRooms) {
        List<ChatRoomMember> foundChatRoomMembers = chatUseCase.getChatRoomMembersByChatRoomIds(
                chatRooms.stream().map(ChatRoom::getId).toList()
        );

        return foundChatRoomMembers
                .stream()
                .collect(Collectors.groupingBy(
                        ChatRoomMember::getChatRoom,
                        Collectors.mapping(chatRoomMember -> chatRoomMember, Collectors.toList())
                ));
    }

    @BatchMapping
    public Map<ChatRoom, List<ChatMessage>> messages(List<ChatRoom> chatRooms) {
        List<ChatMessage> foundChatMessages = chatUseCase.getChatMessageByChatRoomIds(
                chatRooms.stream().map(ChatRoom::getId).toList()
        );

        return foundChatMessages
                .stream()
                .collect(Collectors.groupingBy(
                        ChatMessage::getChatRoom,
                        Collectors.mapping(chatMessage -> chatMessage, Collectors.toList())
                ));
    }

}
