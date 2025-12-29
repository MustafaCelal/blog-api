package com.raptiye.blog.dto;

import com.raptiye.blog.dto.request.*;
import com.raptiye.blog.dto.response.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class DtoBuilderTest {

    @Test
    void testAllRequestDTOBuilders() {
        // LoginRequest
        LoginRequest login = LoginRequest.builder()
                .username("user")
                .password("pass")
                .build();
        assertThat(login.getUsername()).isEqualTo("user");
        assertThat(login.getPassword()).isEqualTo("pass");

        // RegisterRequest
        RegisterRequest register = RegisterRequest.builder()
                .username("user")
                .email("email@test.com")
                .password("password")
                .build();
        assertThat(register.getUsername()).isEqualTo("user");
        assertThat(register.getEmail()).isEqualTo("email@test.com");
        assertThat(register.getPassword()).isEqualTo("password");

        // CreateTagRequest
        CreateTagRequest tag = CreateTagRequest.builder()
                .name("Tag")
                .build();
        assertThat(tag.getName()).isEqualTo("Tag");

        // CreatePostRequest
        CreatePostRequest post = CreatePostRequest.builder()
                .title("Title")
                .summary("Summary")
                .content("Content")
                .tagIds(Collections.singleton(1L))
                .build();
        assertThat(post.getTitle()).isEqualTo("Title");
        assertThat(post.getSummary()).isEqualTo("Summary");
        assertThat(post.getContent()).isEqualTo("Content");
        assertThat(post.getTagIds()).hasSize(1);

        // UpdatePostRequest
        UpdatePostRequest update = UpdatePostRequest.builder()
                .title("Updated")
                .summary("Summary")
                .content("Content")
                .published(true)
                .build();
        assertThat(update.getTitle()).isEqualTo("Updated");
        assertThat(update.getPublished()).isTrue();

        // CreateCommentRequest
        CreateCommentRequest comment = CreateCommentRequest.builder()
                .authorName("Author")
                .authorEmail("author@test.com")
                .content("Comment")
                .build();
        assertThat(comment.getAuthorName()).isEqualTo("Author");
        assertThat(comment.getAuthorEmail()).isEqualTo("author@test.com");
        assertThat(comment.getContent()).isEqualTo("Comment");
    }

    @Test
    void testAllResponseDTOBuilders() {
        LocalDateTime now = LocalDateTime.now();

        // AuthResponse
        AuthResponse auth = AuthResponse.builder()
                .token("token")
                .username("user")
                .email("email@test.com")
                .role("USER")
                .build();
        assertThat(auth.getToken()).isEqualTo("token");
        assertThat(auth.getUsername()).isEqualTo("user");
        assertThat(auth.getEmail()).isEqualTo("email@test.com");
        assertThat(auth.getRole()).isEqualTo("USER");

        // TagResponse
        TagResponse tag = TagResponse.builder()
                .id(1L)
                .name("Tag")
                .slug("tag")
                .postCount(5)
                .createdAt(now)
                .build();
        assertThat(tag.getId()).isEqualTo(1L);
        assertThat(tag.getName()).isEqualTo("Tag");
        assertThat(tag.getSlug()).isEqualTo("tag");
        assertThat(tag.getPostCount()).isEqualTo(5);
        assertThat(tag.getCreatedAt()).isEqualTo(now);

        // CommentResponse
        CommentResponse comment = CommentResponse.builder()
                .id(1L)
                .authorName("Author")
                .content("Content")
                .approved(true)
                .createdAt(now)
                .build();
        assertThat(comment.getId()).isEqualTo(1L);
        assertThat(comment.getAuthorName()).isEqualTo("Author");
        assertThat(comment.getContent()).isEqualTo("Content");
        assertThat(comment.isApproved()).isTrue();
        assertThat(comment.getCreatedAt()).isEqualTo(now);

        // PostResponse
        PostResponse post = PostResponse.builder()
                .id(1L)
                .title("Title")
                .slug("title")
                .summary("Summary")
                .content("Content")
                .published(true)
                .tags(Collections.emptyList())
                .commentCount(3)
                .createdAt(now)
                .updatedAt(now)
                .build();
        assertThat(post.getId()).isEqualTo(1L);
        assertThat(post.getTitle()).isEqualTo("Title");
        assertThat(post.getSlug()).isEqualTo("title");
        assertThat(post.getSummary()).isEqualTo("Summary");
        assertThat(post.getContent()).isEqualTo("Content");
        assertThat(post.isPublished()).isTrue();
        assertThat(post.getTags()).isEmpty();
        assertThat(post.getCommentCount()).isEqualTo(3);
        assertThat(post.getCreatedAt()).isEqualTo(now);
        assertThat(post.getUpdatedAt()).isEqualTo(now);

        // PostDetailResponse
        PostDetailResponse detail = PostDetailResponse.builder()
                .id(1L)
                .title("Title")
                .slug("title")
                .summary("Summary")
                .content("Content")
                .published(true)
                .tags(Collections.emptyList())
                .comments(Collections.emptyList())
                .createdAt(now)
                .updatedAt(now)
                .build();
        assertThat(detail.getId()).isEqualTo(1L);
        assertThat(detail.getTitle()).isEqualTo("Title");
        assertThat(detail.getSlug()).isEqualTo("title");
        assertThat(detail.getSummary()).isEqualTo("Summary");
        assertThat(detail.getContent()).isEqualTo("Content");
        assertThat(detail.isPublished()).isTrue();
        assertThat(detail.getTags()).isEmpty();
        assertThat(detail.getComments()).isEmpty();
        assertThat(detail.getCreatedAt()).isEqualTo(now);
        assertThat(detail.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testDTOEqualsAndHashCode() {
        AuthResponse auth1 = new AuthResponse("t", "u", "e", "r");
        AuthResponse auth2 = new AuthResponse("t", "u", "e", "r");

        assertThat(auth1).isEqualTo(auth2);
        assertThat(auth1.hashCode()).isEqualTo(auth2.hashCode());
        assertThat(auth1.toString()).contains("token");
    }

    @Test
    void testNoArgsConstructors() {
        new LoginRequest();
        new RegisterRequest();
        new CreateTagRequest();
        new CreatePostRequest();
        new UpdatePostRequest();
        new CreateCommentRequest();
        new AuthResponse();
        new TagResponse();
        new CommentResponse();
        new PostResponse();
        new PostDetailResponse();
    }

    @Test
    void testAllArgsConstructors() {
        new LoginRequest("user", "pass");
        new RegisterRequest("user", "email@test.com", "password");
        new CreateTagRequest("Tag");
        new AuthResponse("token", "user", "email", "role");
    }
}
