package com.muazhari.socialmediabackend2.outers.deliveries.controllers;

import com.muazhari.socialmediabackend2.inners.models.entities.*;
import com.muazhari.socialmediabackend2.inners.usecases.ChatUseCase;
import com.muazhari.socialmediabackend2.inners.usecases.PostUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.federation.EntityMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class ContentController {

    @Autowired
    PostUseCase postUseCase;

    @Autowired
    ChatUseCase chatUseCase;

    @BatchMapping
    public Map<Account, List<ChatMessage>> messages(List<Account> accounts) {
        Map<UUID, Account> accountMaps = accounts
                .stream()
                .collect(Collectors.toMap(Account::getId, account -> account, (a, b) -> a));

        List<ChatMessage> foundChatMessages = chatUseCase.getChatMessagesByAccountIds(
                accountMaps.keySet().stream().toList()
        );

        return foundChatMessages
                .stream()
                .collect(Collectors.groupingBy(
                        chatMessage -> accountMaps.get(chatMessage.getAccountId()),
                        Collectors.mapping(chatMessage -> chatMessage, Collectors.toList())
                ));
    }

    @BatchMapping
    public Map<Account, List<ChatRoom>> rooms(List<Account> accounts) {
        Map<UUID, Account> accountMaps = accounts
                .stream()
                .collect(Collectors.toMap(Account::getId, account -> account, (a, b) -> a));

        List<ChatRoomMember> foundChatRoomMembers = chatUseCase.getChatRoomMembersByAccountIds(
                accountMaps.keySet().stream().toList()
        );

        return foundChatRoomMembers
                .stream()
                .collect(Collectors.groupingBy(
                        chatRoomMember -> accountMaps.get(chatRoomMember.getAccountId()),
                        Collectors.mapping(ChatRoomMember::getChatRoom, Collectors.toList())
                ));
    }

    @BatchMapping
    public Map<Account, List<Post>> posts(List<Account> accounts) {
        Map<UUID, Account> accountMaps = accounts
                .stream()
                .collect(Collectors.toMap(Account::getId, account -> account, (a, b) -> a));

        List<Post> foundPosts = postUseCase.getPostsByAccountIds(
                accountMaps.keySet().stream().toList()
        );

        return foundPosts
                .stream()
                .collect(Collectors.groupingBy(
                        post -> accountMaps.get(post.getAccountId()),
                        Collectors.mapping(post -> post, Collectors.toList())
                ));
    }

    @BatchMapping
    public Map<Account, List<PostLike>> postLikes(List<Account> accounts) {
        Map<UUID, Account> accountMaps = accounts
                .stream()
                .collect(Collectors.toMap(Account::getId, account -> account, (a, b) -> a));

        List<PostLike> foundPostLikes = postUseCase.getPostLikesByAccountIds(
                accountMaps.keySet().stream().toList()
        );

        return foundPostLikes
                .stream()
                .collect(Collectors.groupingBy(
                        postLike -> accountMaps.get(postLike.getAccountId()),
                        Collectors.mapping(postLike -> postLike, Collectors.toList())
                ));
    }

    @EntityMapping
    public List<Account> account(@Argument List<UUID> idList) {
        return idList
                .stream()
                .map(id -> Account
                        .builder()
                        .id(id)
                        .build()
                )
                .toList();
    }
}
