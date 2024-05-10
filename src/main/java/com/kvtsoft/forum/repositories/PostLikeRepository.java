package com.kvtsoft.forum.repositories;

import com.kvtsoft.forum.models.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // Custom query to find a PostLike by postId and username
    PostLike findByPostIdAndUsername(Long postId, String username);
}

