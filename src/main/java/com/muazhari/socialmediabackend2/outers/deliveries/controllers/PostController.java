package com.muazhari.socialmediabackend2.outers.deliveries.controllers;

import com.muazhari.socialmediabackend2.inners.models.entities.Post;
import com.muazhari.socialmediabackend2.inners.models.entities.PostLike;
import com.muazhari.socialmediabackend2.inners.usecases.PostUseCase;
import org.springframework.beans.factory.annotation.Autowired;
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
