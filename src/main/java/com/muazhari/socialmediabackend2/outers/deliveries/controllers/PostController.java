package com.muazhari.socialmediabackend2.outers.deliveries.controllers;

import com.muazhari.socialmediabackend2.inners.models.entities.Post;
import com.muazhari.socialmediabackend2.inners.models.entities.PostLike;
import com.muazhari.socialmediabackend2.inners.usecases.PostUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.federation.EntityMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

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

    @SchemaMapping
    public List<PostLike> likes(Post post) {
        return postUseCase.getPostLikesByIds(List.of(post.getId()));
    }

    @EntityMapping
    public List<Post> post(@Argument List<UUID> idList) {
        return postUseCase.getPostsByIds(idList);
    }

    @EntityMapping
    public List<PostLike> postLike(@Argument List<UUID> idList) {
        return postUseCase.getPostLikesByIds(idList);
    }

}
