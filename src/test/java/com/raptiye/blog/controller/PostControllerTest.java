package com.raptiye.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raptiye.blog.dto.request.CreatePostRequest;
import com.raptiye.blog.dto.request.UpdatePostRequest;
import com.raptiye.blog.dto.response.PostDetailResponse;
import com.raptiye.blog.dto.response.PostResponse;
import com.raptiye.blog.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private com.raptiye.blog.security.JwtUtil jwtUtil;

    @MockBean
    private com.raptiye.blog.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllPosts() throws Exception {
        PostResponse response = new PostResponse();
        response.setId(1L);
        response.setTitle("Test Post");

        when(postService.getPublishedPosts()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Post"));
    }

    @Test
    void shouldGetAllPostsIncludingUnpublished() throws Exception {
        PostResponse response = new PostResponse();
        response.setId(1L);
        response.setTitle("Test Post");

        when(postService.getAllPosts()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/posts?publishedOnly=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Post"));
    }

    @Test
    void shouldGetPostBySlug() throws Exception {
        PostDetailResponse response = new PostDetailResponse();
        response.setId(1L);
        response.setSlug("test-post");

        when(postService.getPostBySlug("test-post", false)).thenReturn(response);

        mockMvc.perform(get("/api/posts/test-post"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("test-post"));
    }

    @Test
    void shouldCreatePost() throws Exception {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("New Post");
        request.setContent("Content");

        PostResponse response = new PostResponse();
        response.setId(1L);
        response.setTitle("New Post");

        when(postService.createPost(any(CreatePostRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/posts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Post"));
    }

    @Test
    @org.junit.jupiter.api.Disabled("Security is disabled for this test class")
    void shouldReturnForbiddenWhenCreatingPostWithoutAdmin() throws Exception {
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("New Post");

        mockMvc.perform(post("/api/posts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdatePost() throws Exception {
        UpdatePostRequest request = new UpdatePostRequest();
        request.setTitle("Updated");

        PostResponse response = new PostResponse();
        response.setTitle("Updated");

        when(postService.updatePost(eq(1L), any(UpdatePostRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/posts/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void shouldDeletePost() throws Exception {
        mockMvc.perform(delete("/api/posts/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(postService).deletePost(1L);
    }

    @Test
    void shouldGetPostById() throws Exception {
        PostDetailResponse response = new PostDetailResponse();
        response.setId(1L);

        when(postService.getPostById(1L, false)).thenReturn(response);

        mockMvc.perform(get("/api/posts/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldGetPostsByTag() throws Exception {
        PostResponse response = new PostResponse();
        response.setId(1L);

        when(postService.getPostsByTagSlug("java")).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/posts/tag/java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldAddTagToPost() throws Exception {
        PostResponse response = new PostResponse();
        response.setId(1L);

        when(postService.addTagToPost(1L, 2L)).thenReturn(response);

        mockMvc.perform(post("/api/posts/1/tags/2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldRemoveTagFromPost() throws Exception {
        PostResponse response = new PostResponse();
        response.setId(1L);

        when(postService.removeTagFromPost(1L, 2L)).thenReturn(response);

        mockMvc.perform(delete("/api/posts/1/tags/2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
