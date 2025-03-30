package com.muazhari.socialmediabackend2.outers.deliveries.controllers;

import com.muazhari.socialmediabackend2.inners.models.entities.Account;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatMessage;
import com.muazhari.socialmediabackend2.inners.models.entities.ChatRoom;
import com.muazhari.socialmediabackend2.inners.models.entities.Post;
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

    @BatchMapping(typeName = "Account", field = "messages")
    public Map<Account, List<ChatMessage>> accountMessages(List<Account> accounts) {
        List<UUID> accountIds = accounts
                .stream()
                .map(Account::getId)
                .toList();

        List<ChatMessage> chatMessages = chatUseCase
                .getChatMessagesByAccountIds(accountIds);

        return accounts.stream()
                .collect(Collectors.toMap(
                        account -> account,
                        account -> chatMessages
                                .stream()
                                .filter(chatMessage -> chatMessage.getAccountId().equals(account.getId()))
                                .toList()
                ));
    }

    @BatchMapping(typeName = "Account", field = "rooms")
    public Map<Account, List<ChatRoom>> accountRooms(List<Account> accounts) {
        List<UUID> accountIds = accounts
                .stream()
                .map(Account::getId)
                .toList();

        List<ChatRoom> chatRooms = chatUseCase
                .getChatRoomsByAccountIds(accountIds);

        return accounts.stream()
                .collect(Collectors.toMap(
                        account -> account,
                        account -> chatRooms
                                .stream()
                                .filter(chatRoom -> chatRoom
                                        .getChatRoomMembers()
                                        .stream()
                                        .anyMatch(chatRoomMember -> chatRoomMember.getAccountId().equals(account.getId())))
                                .toList()
                ));
    }

    @BatchMapping(typeName = "Account", field = "posts")
    public Map<Account, List<Post>> accountPosts(List<Account> accounts) {
        List<UUID> accountIds = accounts.stream()
                .map(Account::getId)
                .toList();

        List<Post> posts = postUseCase.getPostsByAccountIds(accountIds);

        return accounts
                .stream()
                .collect(Collectors.toMap(
                        account -> account,
                        account -> posts
                                .stream()
                                .filter(post -> post.getAccountId().equals(account.getId()))
                                .toList()
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
