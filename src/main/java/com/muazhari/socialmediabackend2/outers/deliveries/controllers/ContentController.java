package com.muazhari.socialmediabackend2.outers.deliveries.controllers;

import com.muazhari.socialmediabackend2.inners.models.entities.*;
import com.muazhari.socialmediabackend2.inners.usecases.ChatUseCase;
import com.muazhari.socialmediabackend2.inners.usecases.PostUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.federation.EntityMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
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
        List<ChatMessage> foundChatMessages = chatUseCase.getChatMessagesByAccountIds(
                accounts.stream().map(Account::getId).toList()
        );

        return foundChatMessages
                .stream()
                .collect(Collectors.groupingBy(
                        chatMessage -> Account.builder().id(chatMessage.getAccountId()).build(),
                        Collectors.mapping(chatMessage -> chatMessage, Collectors.toList())
                ));
    }

    @BatchMapping
    public Map<Account, List<ChatRoom>> rooms(List<Account> accounts) {
        List<ChatRoom> foundChatRoom = chatUseCase.getChatRoomsByAccountIds(
                accounts.stream().map(Account::getId).toList()
        );

        return foundChatRoom
                .stream()
                .collect(Collectors.groupingBy(
                        chatRoom -> Account.builder().id(chatRoom.getId()).build(),
                        Collectors.mapping(chatRoom -> chatRoom, Collectors.toList())
                ));
    }

    @BatchMapping
    public Map<Account, List<Post>> posts(List<Account> accounts) {
        List<Post> foundPosts = postUseCase.getPostsByAccountIds(
                accounts.stream().map(Account::getId).toList()
        );

        return foundPosts
                .stream()
                .collect(Collectors.groupingBy(
                        post -> Account.builder().id(post.getAccountId()).build(),
                        Collectors.mapping(post -> post, Collectors.toList())
                ));
    }

    @BatchMapping
    public Map<Account, List<PostLike>> postLikes(List<Account> accounts) {
        List<PostLike> foundPostLikes = postUseCase.getPostLikesByAccountIds(
                accounts.stream().map(Account::getId).toList()
        );

        return foundPostLikes
                .stream()
                .collect(Collectors.groupingBy(
                        postLike -> Account.builder().id(postLike.getAccountId()).build(),
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
