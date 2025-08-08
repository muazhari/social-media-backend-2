package com.muazhari.socialmediabackend2.outers.deliveries.controllers;

import com.muazhari.socialmediabackend2.inners.models.entities.Account;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatMessage;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoom;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoomMember;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.ChatMessageInput;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.ChatRoomInput;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.ChatRoomMemberInput;
import com.muazhari.socialmediabackend2.inners.usecases.ChatUseCase;
import com.muazhari.socialmediabackend2.outers.exceptions.AuthenticationException;
import com.muazhari.socialmediabackend2.outers.exceptions.AuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public List<ChatRoom> myChatRooms(
            @AuthenticationPrincipal Account account
    ) throws Exception {
        if (account == null) {
            throw new AuthenticationException();
        }
        return chatUseCase.getChatRoomsByAccountIds(
                List.of(account.getId())
        );
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
    public ChatRoomMember addMemberToChatRoom(
            @AuthenticationPrincipal Account account,
            @Argument ChatRoomMemberInput input
    ) throws Exception {
        if (account == null) {
            throw new AuthenticationException();
        }

        if (input.getAccountId() == null) {
            input.setAccountId(account.getId());
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = authentication != null
                    && authentication
                    .getAuthorities()
                    .stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"));

            if (!isAdmin && !input.getAccountId().equals(account.getId())) {
                throw new AuthorizationException();
            }
        }

        return chatUseCase.addMemberToChatRoom(input);
    }

    @MutationMapping
    public ChatMessage addChatMessage(
            @AuthenticationPrincipal Account account,
            @Argument ChatMessageInput input
    ) throws Exception {
        if (account == null) {
            throw new AuthenticationException();
        }

        if (input.getAccountId() == null) {
            input.setAccountId(account.getId());
        } else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = authentication != null
                    && authentication
                    .getAuthorities()
                    .stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("admin"));

            if (!isAdmin && !input.getAccountId().equals(account.getId())) {
                throw new AuthorizationException();
            }
        }

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
