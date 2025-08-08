package com.muazhari.socialmediabackend2.outers.deliveries.controllers;

import com.muazhari.socialmediabackend2.inners.models.entities.Account;
import com.muazhari.socialmediabackend2.inners.models.entities.Post;
import com.muazhari.socialmediabackend2.inners.models.entities.PostLike;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.PostInput;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.PostLikeInput;
import com.muazhari.socialmediabackend2.inners.usecases.PostUseCase;
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
            @AuthenticationPrincipal Account account
    ) throws Exception {
        if (account == null) {
            throw new AuthenticationException();
        }
        return postUseCase.getPostsByAccountIds(
                List.of(account.getId())
        );
    }

    @MutationMapping
    public Post addPost(
            @AuthenticationPrincipal Account account,
            @Argument PostInput input
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

        return postUseCase.addPost(input);
    }

    @MutationMapping
    public PostLike likePost(
            @AuthenticationPrincipal Account account,
            @Argument PostLikeInput input
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


        return postUseCase.likePost(input);
    }

    @MutationMapping
    public PostLike unlikePost(
            @AuthenticationPrincipal Account account,
            @Argument PostLikeInput input
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
