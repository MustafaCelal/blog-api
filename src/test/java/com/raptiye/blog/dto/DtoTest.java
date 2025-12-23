package com.raptiye.blog.dto;

import com.raptiye.blog.dto.request.*;
import com.raptiye.blog.dto.response.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void testRequestDTOs() {
        LoginRequest login = new LoginRequest("user", "pass");
        assertThat(login.getUsername()).isEqualTo("user");
        assertThat(login.getPassword()).isEqualTo("pass");

        RegisterRequest register = new RegisterRequest("u", "e@test.com", "password");
        assertThat(register.getUsername()).isEqualTo("u");

        CreateTagRequest tag = new CreateTagRequest("Name");
        assertThat(tag.getName()).isEqualTo("Name");

        CreatePostRequest post = new CreatePostRequest();
        post.setTitle("T");
        assertThat(post.getTitle()).isEqualTo("T");

        UpdatePostRequest update = new UpdatePostRequest();
        update.setTitle("U");
        assertThat(update.getTitle()).isEqualTo("U");

        CreateCommentRequest comment = new CreateCommentRequest();
        comment.setContent("C");
        assertThat(comment.getContent()).isEqualTo("C");
    }

    @Test
    void testResponseDTOs() {
        AuthResponse auth = new AuthResponse("t", "u", "e", "r");
        assertThat(auth.getToken()).isEqualTo("t");

        TagResponse tag = TagResponse.builder().id(1L).name("T").build();
        assertThat(tag.getId()).isEqualTo(1L);

        CommentResponse comment = CommentResponse.builder().id(1L).content("C").build();
        assertThat(comment.getId()).isEqualTo(1L);

        PostResponse post = PostResponse.builder().id(1L).title("T").build();
        assertThat(post.getId()).isEqualTo(1L);

        PostDetailResponse detail = PostDetailResponse.builder()
                .id(1L)
                .comments(Collections.emptyList())
                .tags(Collections.emptyList())
                .build();
        assertThat(detail.getId()).isEqualTo(1L);
    }
}
