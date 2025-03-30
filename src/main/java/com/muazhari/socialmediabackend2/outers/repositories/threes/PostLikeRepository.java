package com.muazhari.socialmediabackend2.outers.repositories.threes;

import com.muazhari.socialmediabackend2.inners.models.entities.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, UUID> {
    Optional<PostLike> findByPostIdAndAccountId(UUID postId, UUID accountId);

    List<PostLike> findAllByPostIdIn(List<UUID> postIds);
}
