package com.raptiye.blog.repository;

import com.raptiye.blog.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdAndApprovedTrueOrderByCreatedAtDesc(Long postId);

    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    List<Comment> findByApprovedFalseOrderByCreatedAtDesc();

    long countByPostIdAndApprovedTrue(Long postId);
}
