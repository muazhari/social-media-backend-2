package com.muazhari.socialmediabackend2.inners.usecases;

import com.muazhari.socialmediabackend2.inners.models.entities.ChatMessage;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoom;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoomMember;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.ChatMessageInput;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.ChatRoomInput;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.ChatRoomMemberInput;
import com.muazhari.socialmediabackend2.outers.configs.FederationConfig;
import com.muazhari.socialmediabackend2.outers.repositories.threes.ChatMessageRepository;
import com.muazhari.socialmediabackend2.outers.repositories.threes.ChatRoomMemberRepository;
import com.muazhari.socialmediabackend2.outers.repositories.threes.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ChatUseCase {
    @Autowired
    ChatMessageRepository chatMessageRepository;
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    ChatRoomMemberRepository chatRoomMemberRepository;
    @Autowired
    FederationConfig federationConfig;

    public List<ChatRoom> getChatRooms() {
        return chatRoomRepository.findAll();
    }

    public List<ChatRoomMember> getChatRoomMembers(UUID chatRoomId) {
        return chatRoomMemberRepository.findAllByChatRoomId(chatRoomId);
    }

    public List<ChatMessage> getChatMessages(UUID chatRoomId) {
        return chatMessageRepository.findAllByChatRoomId(chatRoomId);
    }

    public List<ChatMessage> getChatMessagesByAccountIds(List<UUID> accountIds) {
        return chatMessageRepository.findAllByAccountIdIn(accountIds);
    }

    public List<ChatRoom> getChatRoomsByAccountIds(List<UUID> accountIds) {
        List<ChatRoomMember> foundChatRoomMembers = chatRoomMemberRepository.findAllByAccountIdIn(accountIds);

        return foundChatRoomMembers
                .stream()
                .map(ChatRoomMember::getChatRoom)
                .distinct()
                .toList();
    }

    public ChatRoom addChatRoom(ChatRoomInput input) {
        ChatRoom chatRoom = ChatRoom
                .builder()
                .id(UUID.randomUUID())
                .name(input.getName())
                .description(input.getDescription())
                .build();

        return chatRoomRepository.saveAndFlush(chatRoom);
    }

    public ChatRoomMember addMemberToChatRoom(ChatRoomMemberInput input) {
        ChatRoom foundChatRoom = chatRoomRepository.findById(input.getChatRoomId()).orElseThrow();

        ChatRoomMember chatRoomMember = ChatRoomMember
                .builder()
                .id(UUID.randomUUID())
                .chatRoom(foundChatRoom)
                .accountId(input.getAccountId())
                .build();

        return chatRoomMemberRepository.saveAndFlush(chatRoomMember);
    }

    public ChatMessage addChatMessage(ChatMessageInput input) {
        ChatRoom foundChatRoom = chatRoomRepository.findById(input.getChatRoomId()).orElseThrow();

        ChatMessage chatMessage = ChatMessage
                .builder()
                .id(UUID.randomUUID())
                .chatRoom(foundChatRoom)
                .accountId(input.getAccountId())
                .content(input.getContent())
                .build();

        ChatMessage createdChatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        federationConfig.
                getHttpGraphQlClient()
                .document("mutation($accountId: ID!){ publishChatMessageIncrement(accountId: $accountId){ success } }")
                .variables(Map.of("accountId", input.getAccountId().toString()))
                .retrieve("publishChatMessageIncrement.success")
                .toEntity(Boolean.class)
                .block();

        return createdChatMessage;
    }

    public List<ChatMessage> getChatMessagesByIds(List<UUID> chatMessageIds) {
        return chatMessageRepository.findAllByIdIn(chatMessageIds);
    }

    public List<ChatRoomMember> getChatRoomMembersByIds(List<UUID> chatRoomMemberIds) {
        return chatRoomMemberRepository.findAllByIdIn(chatRoomMemberIds);
    }

    public List<ChatRoomMember> getChatRoomMembersByChatRoomIds(List<UUID> chatRoomIds) {
        return chatRoomMemberRepository.findAllByChatRoomIdIn(chatRoomIds);
    }

    public List<ChatMessage> getChatMessageByChatRoomIds(List<UUID> chatRoomIds) {
        return chatMessageRepository.findAllByChatRoomIdIn(chatRoomIds);
    }
}
