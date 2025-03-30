package com.muazhari.socialmediabackend2.outers.deliveries.controllers;

import com.muazhari.socialmediabackend2.inners.models.entities.Account;
import com.muazhari.socialmediabackend2.inners.models.entities.Post;
import com.muazhari.socialmediabackend2.inners.models.entities.PostLike;
import com.muazhari.socialmediabackend2.inners.usecases.PostUseCase;
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
public class PostController {

    @Autowired
    PostUseCase postUseCase;

    @QueryMapping
    public List<Post> posts() {
        return postUseCase.getPosts();
    }

    @MutationMapping
    public Post addPost(@Argument UUID accountId, @Argument String title, @Argument String content) {
        return postUseCase.addPost(accountId, title, content);
    }

    @MutationMapping
    public PostLike likePost(@Argument UUID postId, @Argument UUID accountId) {
        return postUseCase.likePost(postId, accountId);
    }

    @MutationMapping
    public PostLike unlikePost(@Argument UUID postId, @Argument UUID accountId) {
        return postUseCase.unlikePost(postId, accountId);
    }

    @BatchMapping(typeName = "Post", field = "account")
    public Map<Post, Account> postAccount(List<Post> posts) {
        return posts.stream()
                .collect(Collectors.toMap(
                        post -> post,
                        post -> Account
                                .builder()
                                .id(post.getAccountId())
                                .build()
                ));
    }

    @BatchMapping(typeName = "Post", field = "likes")
    public Map<Post, List<PostLike>> postLikes(List<Post> posts) {
        List<UUID> postIds = posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());

        List<PostLike> postLikes = postUseCase.getPostLikes(postIds);

        return posts
                .stream()
                .collect(Collectors.toMap(
                        post -> post,
                        post -> postLikes
                                .stream()
                                .filter(postLike -> postLike.getPost().getId().equals(post.getId()))
                                .collect(Collectors.toList())
                ));
    }

    @BatchMapping(typeName = "PostLike", field = "account")
    public Map<PostLike, Account> postLikeAccount(List<PostLike> postLikes) {
        return postLikes.stream()
                .collect(Collectors.toMap(
                        postLike -> postLike,
                        postLike -> Account
                                .builder()
                                .id(postLike.getAccountId())
                                .build()
                ));
    }

    @BatchMapping(typeName = "Account", field = "posts")
    public Map<Account, List<Post>> accountPosts(List<Account> accounts) {
        List<UUID> accountIds = accounts.stream()
                .map(Account::getId)
                .collect(Collectors.toList());

        List<Post> posts = postUseCase.getPostsByAccountIds(accountIds);

        return accounts
                .stream()
                .collect(Collectors.toMap(
                        account -> account,
                        account -> posts
                                .stream()
                                .filter(post -> post.getAccountId().equals(account.getId()))
                                .collect(Collectors.toList())
                ));
    }

}
