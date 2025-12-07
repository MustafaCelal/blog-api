package com.raptiye.blog.repository;

import com.raptiye.blog.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByPublishedTrueOrderByCreatedAtDesc();

    List<Post> findAllByOrderByCreatedAtDesc();

    Optional<Post> findBySlug(String slug);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.slug = :tagSlug AND p.published = true ORDER BY p.createdAt DESC")
    List<Post> findByTagSlug(@Param("tagSlug") String tagSlug);

    boolean existsBySlug(String slug);
}
