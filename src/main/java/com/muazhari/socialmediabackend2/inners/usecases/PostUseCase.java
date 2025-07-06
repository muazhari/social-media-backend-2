package com.muazhari.socialmediabackend2.inners.usecases;

import com.muazhari.socialmediabackend2.inners.models.entities.Post;
import com.muazhari.socialmediabackend2.inners.models.entities.PostLike;
import com.muazhari.socialmediabackend2.outers.repositories.FileRepository;
import com.muazhari.socialmediabackend2.outers.repositories.threes.PostLikeRepository;
import com.muazhari.socialmediabackend2.outers.repositories.threes.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class PostUseCase {
    @Autowired
    PostRepository postRepository;
    @Autowired
    PostLikeRepository postLikeRepository;

    @Autowired
    FileRepository fileRepository;

    public List<Post> getPosts() {
        List<Post> foundPosts = postRepository.findAll();

        for (Post foundPost : foundPosts) {
            if (foundPost.getImageId() != null) {
                String imageUrl = fileRepository.getFileUrl(foundPost.getImageId().toString());
                foundPost.setImageUrl(imageUrl);
            }
        }

        return foundPosts;
    }

    public Post addPost(UUID accountId, String title, String content, MultipartFile image) {
        UUID imageId = UUID.randomUUID();
        fileRepository.uploadFile(imageId.toString(), image);

        Post post = Post
                .builder()
                .id(UUID.randomUUID())
                .accountId(accountId)
                .title(title)
                .content(content)
                .imageId(imageId)
                .build();

        Post createdPost = postRepository.saveAndFlush(post);
        String imageUrl = fileRepository.getFileUrl(post.getImageId().toString());
        createdPost.setImageUrl(imageUrl);

        return createdPost;
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
        List<Post> foundPosts = postRepository.findAllByAccountIdIn(accountIds);

        for (Post foundPost : foundPosts) {
            if (foundPost.getImageId() != null) {
                String imageUrl = fileRepository.getFileUrl(foundPost.getImageId().toString());
                foundPost.setImageUrl(imageUrl);
            }
        }

        return foundPosts;
    }

    public List<PostLike> getPostLikesByAccountIds(List<UUID> accountIds) {
        return postLikeRepository.findAllByAccountIdIn(accountIds);
    }

    public List<PostLike> getPostLikesByPostIds(List<UUID> postIds) {
        return postLikeRepository.findAllByPostIdIn(postIds);
    }
}
