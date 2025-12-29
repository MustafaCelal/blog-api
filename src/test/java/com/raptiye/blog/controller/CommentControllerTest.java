package com.raptiye.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raptiye.blog.dto.request.CreateCommentRequest;
import com.raptiye.blog.dto.response.CommentResponse;
import com.raptiye.blog.service.CommentService;
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

@WebMvcTest(CommentController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private com.raptiye.blog.security.JwtUtil jwtUtil;

    @MockBean
    private com.raptiye.blog.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetCommentsByPostId() throws Exception {
        CommentResponse response = new CommentResponse();
        response.setId(1L);

        when(commentService.getApprovedCommentsByPostId(1L)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldGetAllCommentsByPostId() throws Exception {
        CommentResponse response = new CommentResponse();
        response.setId(1L);

        when(commentService.getAllCommentsByPostId(1L)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/posts/1/comments?includeAll=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldGetPendingComments() throws Exception {
        CommentResponse response = new CommentResponse();
        response.setId(1L);

        when(commentService.getPendingComments()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/comments/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldCreateComment() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setAuthorName("John");
        request.setContent("Cool");

        CommentResponse response = new CommentResponse();
        response.setId(1L);

        when(commentService.createComment(eq(1L), any(CreateCommentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/posts/1/comments")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldApproveComment() throws Exception {
        CommentResponse response = new CommentResponse();
        response.setId(1L);
        response.setApproved(true);

        when(commentService.approveComment(1L)).thenReturn(response);

        mockMvc.perform(put("/api/comments/1/approve").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.approved").value(true));
    }

    @Test
    void shouldDeleteComment() throws Exception {
        mockMvc.perform(delete("/api/comments/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(commentService).deleteComment(1L);
    }
}
