package com.raptiye.blog.controller;

import com.raptiye.blog.dto.response.PostDetailResponse;
import com.raptiye.blog.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WebController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class WebControllerTest {

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

    @MockBean(name = "markdownService")
    private com.raptiye.blog.util.MarkdownService markdownService;

    @Test
    void shouldReturnHomeView() throws Exception {
        when(postService.getPublishedPosts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("posts"));
    }

    @Test
    void shouldReturnPostDetailView() throws Exception {
        PostDetailResponse response = new PostDetailResponse();
        when(postService.getPostBySlug("test-slug", true)).thenReturn(response);

        mockMvc.perform(get("/post/test-slug"))
                .andExpect(status().isOk())
                .andExpect(view().name("post-detail"))
                .andExpect(model().attributeExists("post"));
    }

    @Test
    void shouldReturnDesignTestView() throws Exception {
        when(postService.getPublishedPostSnippets()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/design-test"))
                .andExpect(status().isOk())
                .andExpect(view().name("bootstrap_blog"))
                .andExpect(model().attributeExists("posts"));
    }
}
