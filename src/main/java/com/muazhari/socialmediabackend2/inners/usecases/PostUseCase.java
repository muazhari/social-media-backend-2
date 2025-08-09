package com.muazhari.socialmediabackend2.inners.usecases;

import com.muazhari.socialmediabackend2.inners.models.entities.Post;
import com.muazhari.socialmediabackend2.inners.models.entities.PostLike;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.PostInput;
import com.muazhari.socialmediabackend2.inners.models.valueobjects.PostLikeInput;
import com.muazhari.socialmediabackend2.outers.repositories.FileRepository;
import com.muazhari.socialmediabackend2.outers.repositories.threes.PostLikeRepository;
import com.muazhari.socialmediabackend2.outers.repositories.threes.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostUseCase {
    final String bucketName = "social-media-backend.post";

    @Autowired
    PostRepository postRepository;
    @Autowired
    PostLikeRepository postLikeRepository;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public List<Post> getPosts() {
        List<Post> foundPosts = postRepository.findAll();

        for (Post foundPost : foundPosts) {
            if (foundPost.getImageId() != null) {
                String imageUrl = fileRepository.getUrl(bucketName, foundPost.getImageId().toString());
                foundPost.setImageUrl(imageUrl);
            }
        }

        return foundPosts;
    }

    public Post addPost(PostInput input) {
        Post post = Post
                .builder()
                .id(UUID.randomUUID())
                .accountId(input.getAccountId())
                .title(input.getTitle())
                .content(input.getContent())
                .build();

        if (input.getImage() != null) {
            post.setImageId(UUID.randomUUID());
            HashMap<String, String> extensionToContentType = new HashMap<>(Map.of(
                    "jpg", "image/jpeg",
                    "jpeg", "image/jpeg",
                    "png", "image/png"
            ));
            String fileName = Objects.requireNonNull(input.getImage().getOriginalFilename());
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            String contentType = extensionToContentType.get(fileExtension);
            if (contentType == null) {
                throw new IllegalArgumentException("Unsupported file extension: " + fileExtension);
            }
            fileRepository.upload(bucketName, post.getImageId().toString(), input.getImage(), contentType);
        }

        Post createdPost = postRepository.saveAndFlush(post);

        if (createdPost.getImageId() != null) {
            String imageUrl = fileRepository.getUrl(bucketName, createdPost.getImageId().toString());
            createdPost.setImageUrl(imageUrl);
        }

        return createdPost;
    }

    public PostLike likePost(PostLikeInput input) {
        Post foundPost = postRepository.findById(input.getPostId()).orElseThrow();
        PostLike postLike = PostLike
                .builder()
                .id(UUID.randomUUID())
                .post(foundPost)
                .accountId(input.getAccountId())
                .build();

        PostLike created = postLikeRepository.saveAndFlush(postLike);

        kafkaTemplate.send(
                "postLike.increment",
                Map.of("account_id", input.getAccountId())
        );

        return created;
    }

    public PostLike unlikePost(PostLikeInput input) {
        List<PostLike> foundPostLikes = postLikeRepository.findAllByPostIdAndAccountId(input.getPostId(), input.getAccountId());
        postLikeRepository.deleteAll(foundPostLikes);

        kafkaTemplate.send(
                "postLike.decrement",
                Map.of("account_id", input.getAccountId())
        );

        return foundPostLikes.getFirst();
    }

    public List<Post> getPostsByAccountIds(List<UUID> accountIds) {
        List<Post> foundPosts = postRepository.findAllByAccountIdIn(accountIds);

        for (Post foundPost : foundPosts) {
            if (foundPost.getImageId() != null) {
                String imageUrl = fileRepository.getUrl(bucketName, foundPost.getImageId().toString());
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
