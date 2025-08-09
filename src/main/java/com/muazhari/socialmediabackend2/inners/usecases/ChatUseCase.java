package com.muazhari.socialmediabackend2.inners.usecases;

import com.muazhari.socialmediabackend2.inners.models.entities.ChatMessage;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoom;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoomMember;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.ChatMessageInput;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.ChatRoomInput;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.ChatRoomMemberInput;
import com.muazhari.socialmediabackend2.outers.repositories.threes.ChatMessageRepository;
import com.muazhari.socialmediabackend2.outers.repositories.threes.ChatRoomMemberRepository;
import com.muazhari.socialmediabackend2.outers.repositories.threes.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
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
    KafkaTemplate<String, Object> kafkaTemplate;

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
                .name(input.getName())
                .description(input.getDescription())
                .build();

        return chatRoomRepository.saveAndFlush(chatRoom);
    }

    public ChatRoomMember addMemberToChatRoom(ChatRoomMemberInput input) {
        ChatRoom foundChatRoom = chatRoomRepository.findById(input.getChatRoomId()).orElseThrow();

        ChatRoomMember chatRoomMember = ChatRoomMember
                .builder()
                .chatRoom(foundChatRoom)
                .accountId(input.getAccountId())
                .build();

        return chatRoomMemberRepository.saveAndFlush(chatRoomMember);
    }

    public ChatMessage addChatMessage(ChatMessageInput input) {
        ChatRoom foundChatRoom = chatRoomRepository.findById(input.getChatRoomId()).orElseThrow();

        ChatMessage chatMessage = ChatMessage
                .builder()
                .chatRoom(foundChatRoom)
                .accountId(input.getAccountId())
                .content(input.getContent())
                .build();

        ChatMessage created = chatMessageRepository.saveAndFlush(chatMessage);

        kafkaTemplate.send(
                "chatMessage.increment",
                Map.of("account_id", input.getAccountId())
        );

        return created;
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
