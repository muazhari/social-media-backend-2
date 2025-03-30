package com.muazhari.socialmediabackend2.outers.deliveries.controllers;

import com.muazhari.socialmediabackend2.inners.models.entities.Account;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatMessage;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoom;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoomMember;
import com.muazhari.socialmediabackend2.inners.usecases.ChatUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.federation.EntityMapping;
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

    @BatchMapping(typeName = "ChatMessage", field = "account")
    public Map<ChatMessage, Account> chatMessageAccount(List<ChatMessage> chatMessages) {
        List<UUID> accountIds = chatMessages
                .stream()
                .map(ChatMessage::getAccountId)
                .toList();

        return chatMessages
                .stream()
                .collect(Collectors.toMap(
                        chatMessage -> chatMessage,
                        chatMessage -> Account.builder().id(chatMessage.getAccountId()).build()
                ));
    }

    @BatchMapping(typeName = "ChatMessage", field = "room")
    public Map<ChatMessage, ChatRoom> chatMessageRoom(List<ChatMessage> chatMessages) {
        List<ChatRoom> chatRooms = chatMessages
                .stream()
                .map(ChatMessage::getChatRoom)
                .toList();

        return chatMessages.
                stream()
                .collect(Collectors.toMap(
                        chatMessage -> chatMessage,
                        chatMessage -> chatRooms
                                .stream()
                                .filter(chatRoom -> chatRoom.getId().equals(chatMessage.getChatRoom().getId()))
                                .findFirst()
                                .orElseThrow()
                ));
    }

    @BatchMapping(typeName = "ChatRoomMember", field = "account")
    public Map<ChatRoomMember, Account> chatRoomMemberAccount(List<ChatRoomMember> chatRoomMembers) {
        List<UUID> accountIds = chatRoomMembers
                .stream()
                .map(ChatRoomMember::getAccountId)
                .toList();

        return chatRoomMembers
                .stream()
                .collect(Collectors.toMap(
                        chatRoomMember -> chatRoomMember,
                        chatRoomMember -> Account.builder().id(chatRoomMember.getAccountId()).build()
                ));
    }

    @BatchMapping(typeName = "ChatRoomMember", field = "room")
    public Map<ChatRoomMember, ChatRoom> chatRoomMemberRoom(List<ChatRoomMember> chatRoomMembers) {
        List<ChatRoom> chatRooms = chatRoomMembers
                .stream()
                .map(ChatRoomMember::getChatRoom)
                .toList();

        return chatRoomMembers
                .stream()
                .collect(Collectors.toMap(
                        chatRoomMember -> chatRoomMember,
                        chatRoomMember -> chatRooms
                                .stream()
                                .filter(chatRoom -> chatRoom.getId().equals(chatRoomMember.getChatRoom().getId()))
                                .findFirst()
                                .orElseThrow()
                ));
    }

    @BatchMapping(typeName = "ChatRoom", field = "members")
    public Map<ChatRoom, List<ChatRoomMember>> chatRoomMembers(List<ChatRoom> chatRooms) {
        List<ChatRoomMember> chatRoomMembers = chatRooms
                .stream()
                .flatMap(chatRoom -> chatRoom.getChatRoomMembers().stream())
                .toList();

        return chatRooms
                .stream()
                .collect(Collectors.toMap(
                        chatRoom -> chatRoom,
                        chatRoom -> chatRoomMembers
                                .stream()
                                .filter(chatRoomMember -> chatRoomMember.getChatRoom().getId().equals(chatRoom.getId()))
                                .toList()
                ));
    }

    @BatchMapping(typeName = "ChatRoom", field = "messages")
    public Map<ChatRoom, List<ChatMessage>> chatRoomMessages(List<ChatRoom> chatRooms) {
        List<ChatMessage> chatMessages = chatRooms
                .stream()
                .flatMap(chatRoom -> chatRoom.getChatMessages().stream())
                .toList();

        return chatRooms.stream()
                .collect(Collectors.toMap(
                        chatRoom -> chatRoom,
                        chatRoom -> chatMessages
                                .stream()
                                .filter(chatMessage -> chatMessage.getChatRoom().getId().equals(chatRoom.getId()))
                                .toList()
                ));
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
