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
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class ContentController {

    @Autowired
    PostUseCase postUseCase;

    @Autowired
    ChatUseCase chatUseCase;

    @SchemaMapping
    public List<ChatMessage> messages(Account account) {
        return chatUseCase.getChatMessagesByAccountIds(List.of(account.getId()));
    }

    @SchemaMapping
    public List<ChatRoom> rooms(Account account) {
        return chatUseCase.getChatRoomsByAccountIds(List.of(account.getId()));
    }

    @SchemaMapping
    public List<Post> posts(Account account) {
        return postUseCase.getPostsByAccountIds(List.of(account.getId()));
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
