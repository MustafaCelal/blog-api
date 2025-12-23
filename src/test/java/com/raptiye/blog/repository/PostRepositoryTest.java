package com.raptiye.blog.repository;

import com.raptiye.blog.config.JpaConfig;
import com.raptiye.blog.domain.Post;
import com.raptiye.blog.domain.Tag;
import com.raptiye.blog.dto.response.PostResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
class PostRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    @Test
    void shouldFindPublishedPostsOrderByCreatedAtDesc() {
        Post post1 = Post.builder().title("Post 1").content("Content 1").published(true).build();
        post1.setCreatedAt(LocalDateTime.now().minusDays(1));

        Post post2 = Post.builder().title("Post 2").content("Content 2").published(false).build();

        Post post3 = Post.builder().title("Post 3").content("Content 3").published(true).build();
        post3.setCreatedAt(LocalDateTime.now().plusDays(1));

        entityManager.persist(post1);
        entityManager.persist(post2);
        entityManager.persist(post3);
        entityManager.flush();

        List<Post> publishedPosts = postRepository.findByPublishedTrueOrderByCreatedAtDesc();

        assertThat(publishedPosts).hasSize(2);
        assertThat(publishedPosts.get(0).getTitle()).isEqualTo("Post 3");
        assertThat(publishedPosts.get(1).getTitle()).isEqualTo("Post 1");
    }

    @Test
    void shouldFindBySlug() {
        Post post = Post.builder().title("My Slug Test").content("Content").slug("my-slug-test").build();
        entityManager.persist(post);
        entityManager.flush();

        Optional<Post> found = postRepository.findBySlug("my-slug-test");

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("My Slug Test");
    }

    @Test
    void shouldCheckIfSlugExists() {
        Post post = Post.builder().title("Exists Test").content("Content").slug("exists-test").build();
        entityManager.persist(post);
        entityManager.flush();

        assertThat(postRepository.existsBySlug("exists-test")).isTrue();
        assertThat(postRepository.existsBySlug("not-exists")).isFalse();
    }

    @Test
    void shouldFindPostsByTagSlug() {
        Tag tag = Tag.builder().name("Java").slug("java").build();
        entityManager.persist(tag);

        Post post = Post.builder().title("Java Post").content("Content").published(true).build();
        post.addTag(tag);
        entityManager.persist(post);

        Post otherPost = Post.builder().title("Other Post").content("Content").published(true).build();
        entityManager.persist(otherPost);

        entityManager.flush();

        List<Post> javaPosts = postRepository.findByTagSlug("java");

        assertThat(javaPosts).hasSize(1);
        assertThat(javaPosts.get(0).getTitle()).isEqualTo("Java Post");
    }

    @Test
    void shouldFindPublishedPostResponsesWithSnippet() {
        Post post = Post.builder()
                .title("Snippet Test")
                .content("This is a very long content that should be truncated.")
                .summary("Summary")
                .published(true)
                .build();
        entityManager.persist(post);
        entityManager.flush();

        List<PostResponse> responses = postRepository.findPublishedPostResponsesWithSnippet(10);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getContent()).isEqualTo("This is a ");
    }
}
