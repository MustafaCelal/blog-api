package com.raptiye.blog.repository;

import com.raptiye.blog.domain.Comment;
import com.raptiye.blog.domain.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.raptiye.blog.config.JpaConfig.class)
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void shouldFindCommentsByPostId() {
        Post post = Post.builder().title("Post").content("Content").build();
        entityManager.persist(post);

        Comment c1 = Comment.builder().authorName("A1").authorEmail("a1@test.com").content("C1").post(post)
                .approved(true).build();
        Comment c2 = Comment.builder().authorName("A2").authorEmail("a2@test.com").content("C2").post(post)
                .approved(false).build();
        entityManager.persist(c1);
        entityManager.persist(c2);
        entityManager.flush();

        List<Comment> approved = commentRepository.findByPostIdAndApprovedTrueOrderByCreatedAtDesc(post.getId());
        List<Comment> all = commentRepository.findByPostIdOrderByCreatedAtDesc(post.getId());

        assertThat(approved).hasSize(1);
        assertThat(all).hasSize(2);
    }

    @Test
    void shouldFindPendingComments() {
        Post post = Post.builder().title("Post").content("Content").build();
        entityManager.persist(post);

        Comment c1 = Comment.builder().authorName("A1").authorEmail("a1@test.com").content("C1").post(post)
                .approved(true).build();
        Comment c2 = Comment.builder().authorName("A2").authorEmail("a2@test.com").content("C2").post(post)
                .approved(false).build();
        entityManager.persist(c1);
        entityManager.persist(c2);
        entityManager.flush();

        List<Comment> pending = commentRepository.findByApprovedFalseOrderByCreatedAtDesc();

        assertThat(pending).hasSize(1);
        assertThat(pending.get(0).getAuthorName()).isEqualTo("A2");
    }

    @Test
    void shouldCountApprovedComments() {
        Post post = Post.builder().title("Post").content("Content").build();
        entityManager.persist(post);

        Comment c1 = Comment.builder().authorName("A1").authorEmail("a1@test.com").content("C1").post(post)
                .approved(true).build();
        Comment c2 = Comment.builder().authorName("A2").authorEmail("a2@test.com").content("C2").post(post)
                .approved(true).build();
        entityManager.persist(c1);
        entityManager.persist(c2);
        entityManager.flush();

        long count = commentRepository.countByPostIdAndApprovedTrue(post.getId());

        assertThat(count).isEqualTo(2);
    }
}
