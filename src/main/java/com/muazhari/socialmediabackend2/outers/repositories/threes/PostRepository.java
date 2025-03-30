package com.muazhari.socialmediabackend2.outers.repositories.threes;

import com.muazhari.socialmediabackend2.inners.models.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findAllByAccountIdIn(List<UUID> accountIds);

    List<Post> findAllByIdIn(List<UUID> postIds);
}
