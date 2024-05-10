package com.kvtsoft.forum.repositories;

import com.kvtsoft.forum.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Custom query to find top 10 posts by username, ordered by createdAt descending
    List<Post> findTop10ByUsernameOrderByCreatedAtDesc(String username);

    // Custom query to find top 10 posts ordered by createdAt descending
    List<Post> findTop10ByOrderByCreatedAtDesc();

    Optional<Post> findById(Long id);
}

