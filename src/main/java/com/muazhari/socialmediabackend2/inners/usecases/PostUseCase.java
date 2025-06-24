package com.muazhari.socialmediabackend2.inners.usecases;

import com.muazhari.socialmediabackend2.inners.models.entities.Post;
import com.muazhari.socialmediabackend2.inners.models.entities.PostLike;
import com.muazhari.socialmediabackend2.outers.repositories.threes.PostLikeRepository;
import com.muazhari.socialmediabackend2.outers.repositories.threes.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PostUseCase {
    @Autowired
    PostRepository postRepository;
    @Autowired
    PostLikeRepository postLikeRepository;

    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    public Post addPost(UUID accountId, String title, String content) {
        Post post = Post
                .builder()
                .accountId(accountId)
                .title(title)
                .content(content)
                .build();

        return postRepository.saveAndFlush(post);
    }

    public PostLike likePost(UUID postId, UUID accountId) {
        Post foundPost = postRepository.findById(postId).orElseThrow();
        PostLike postLike = PostLike
                .builder()
                .post(foundPost)
                .accountId(accountId)
                .build();

        return postLikeRepository.saveAndFlush(postLike);
    }

    public PostLike unlikePost(UUID postId, UUID accountId) {
        PostLike foundPostLike = postLikeRepository.findByPostIdAndAccountId(postId, accountId).orElseThrow();
        postLikeRepository.delete(foundPostLike);

        return foundPostLike;
    }

    public List<Post> getPostsByAccountIds(List<UUID> accountIds) {
        return postRepository.findAllByAccountIdIn(accountIds);
    }

    public List<PostLike> getPostLikesByAccountIds(List<UUID> accountIds) {
        return postLikeRepository.findAllByAccountIdIn(accountIds);
    }

    public List<PostLike> getPostLikesByPostIds(List<UUID> postIds) {
        return postLikeRepository.findAllByPostIdIn(postIds);
    }
}
