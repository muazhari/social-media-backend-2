package com.muazhari.socialmediabackend2.outers.deliveries.controllers;

import com.muazhari.socialmediabackend2.inners.models.entities.Account;
import com.muazhari.socialmediabackend2.inners.models.entities.Post;
import com.muazhari.socialmediabackend2.inners.models.entities.PostLike;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.PostInput;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.PostLikeInput;
import com.muazhari.socialmediabackend2.inners.usecases.PostUseCase;
import com.muazhari.socialmediabackend2.outers.exceptions.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PostController {

    @Autowired
    PostUseCase postUseCase;

    @QueryMapping
    public List<Post> posts() {
        return postUseCase.getPosts();
    }

    @QueryMapping
    public List<Post> myPosts(
            Authentication authentication
    ) throws Exception {
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        if (!isAuthenticated) {
            throw new AuthenticationException();
        }

        Account account = (Account) authentication.getPrincipal();

        return postUseCase.getPostsByAccountIds(
                List.of(account.getId())
        );
    }

    @MutationMapping
    public Post addMyPost(
            Authentication authentication,
            @Argument PostInput input
    ) throws Exception {
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        if (!isAuthenticated) {
            throw new AuthenticationException();
        }

        Account account = (Account) authentication.getPrincipal();

        input.setAccountId(account.getId());

        return postUseCase.addPost(input);
    }

    @MutationMapping
    public PostLike likePost(
            Authentication authentication,
            @Argument PostLikeInput input
    ) throws Exception {
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        if (!isAuthenticated) {
            throw new AuthenticationException();
        }

        Account account = (Account) authentication.getPrincipal();
        input.setAccountId(account.getId());

        return postUseCase.likePost(input);
    }

    @MutationMapping
    public PostLike unlikePost(
            Authentication authentication,
            @Argument PostLikeInput input
    ) throws Exception {
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        if (!isAuthenticated) {
            throw new AuthenticationException();
        }

        Account account = (Account) authentication.getPrincipal();
        input.setAccountId(account.getId());

        return postUseCase.unlikePost(input);
    }

    @BatchMapping
    public Map<Post, List<PostLike>> likes(List<Post> posts) {
        List<PostLike> postLikes = postUseCase.getPostLikesByPostIds(
                posts.stream().map(Post::getId).toList()
        );

        return postLikes
                .stream()
                .collect(Collectors.groupingBy(
                        PostLike::getPost,
                        Collectors.mapping(postLike -> postLike, Collectors.toList())
                ));
    }

}
