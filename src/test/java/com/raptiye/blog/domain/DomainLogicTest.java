package com.raptiye.blog.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class DomainLogicTest {

    @Test
    void postSlugGeneration() {
        Post post = Post.builder().title("Hello World! @2023").build();
        post.onCreate();
        assertThat(post.getSlug()).isEqualTo("hello-world-2023");

        post.setSlug("custom-slug");
        post.onCreate();
        assertThat(post.getSlug()).isEqualTo("custom-slug");

        Post emptyPost = new Post();
        emptyPost.onCreate(); // Should not crash
    }

    @Test
    void postEqualsAndHashCode() {
        Post p1 = Post.builder().id(1L).build();
        Post p2 = Post.builder().id(1L).build();
        Post p3 = Post.builder().id(2L).build();

        assertThat(p1).isEqualTo(p2);
        assertThat(p1).isNotEqualTo(p3);
        assertThat(p1).isNotEqualTo(null);
        assertThat(p1).isNotEqualTo(new Object());
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }

    @Test
    void tagSlugGeneration() {
        Tag tag = Tag.builder().name("Spring Boot !!!").build();
        tag.onCreate();
        assertThat(tag.getSlug()).isEqualTo("spring-boot");
    }

    @Test
    void tagEqualsAndHashCode() {
        Tag t1 = Tag.builder().id(1L).build();
        Tag t2 = Tag.builder().id(1L).build();

        assertThat(t1).isEqualTo(t2);
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
        assertThat(t1).isNotEqualTo(new Tag());
    }

    @Test
    void userEqualsAndHashCode() {
        User u1 = User.builder().id(1L).build();
        User u2 = User.builder().id(1L).build();

        assertThat(u1).isEqualTo(u2);
        assertThat(u1.hashCode()).isEqualTo(u2.hashCode());
    }

    @Test
    void commentEqualsAndHashCode() {
        Comment c1 = Comment.builder().id(1L).build();
        Comment c2 = Comment.builder().id(1L).build();

        assertThat(c1).isEqualTo(c2);
        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
    }
}
