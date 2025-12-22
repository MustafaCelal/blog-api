package com.raptiye.blog.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class CommentTest {

    @Test
    void shouldCreateCommentWithBuilder() {
        Comment comment = Comment.builder()
                .authorName("Jane Doe")
                .authorEmail("jane@example.com")
                .content("Great post!")
                .approved(true)
                .build();

        assertThat(comment.getAuthorName()).isEqualTo("Jane Doe");
        assertThat(comment.getAuthorEmail()).isEqualTo("jane@example.com");
        assertThat(comment.getContent()).isEqualTo("Great post!");
        assertThat(comment.isApproved()).isTrue();
    }

    @Test
    void shouldHandleEqualityCorrectly() {
        Comment comment1 = Comment.builder().id(1L).build();
        Comment comment2 = Comment.builder().id(1L).build();
        Comment comment3 = Comment.builder().id(2L).build();

        assertThat(comment1).isEqualTo(comment2);
        assertThat(comment1).isNotEqualTo(comment3);
    }
}
