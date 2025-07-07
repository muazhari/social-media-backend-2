package com.muazhari.socialmediabackend2.outers.deliveries.controllers;

import com.muazhari.socialmediabackend2.inners.models.entities.Post;
import com.muazhari.socialmediabackend2.inners.models.entities.PostLike;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.PostInput;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.PostLikeInput;
import com.muazhari.socialmediabackend2.inners.usecases.PostUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
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

    @MutationMapping
    public Post addPost(@Argument PostInput input) {
        return postUseCase.addPost(input);
    }

    @MutationMapping
    public PostLike likePost(@Argument PostLikeInput input) {
        return postUseCase.likePost(input);
    }

    @MutationMapping
    public PostLike unlikePost(@Argument PostLikeInput input) {
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
