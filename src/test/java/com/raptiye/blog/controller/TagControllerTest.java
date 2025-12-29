package com.raptiye.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raptiye.blog.dto.request.CreateTagRequest;
import com.raptiye.blog.dto.response.TagResponse;
import com.raptiye.blog.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private com.raptiye.blog.security.JwtUtil jwtUtil;

    @MockBean
    private com.raptiye.blog.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllTags() throws Exception {
        TagResponse response = new TagResponse();
        response.setId(1L);
        response.setName("Java");

        when(tagService.getAllTags()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Java"));
    }

    @Test
    void shouldGetTagBySlug() throws Exception {
        TagResponse response = new TagResponse();
        response.setId(1L);
        response.setSlug("java");

        when(tagService.getTagBySlug("java")).thenReturn(response);

        mockMvc.perform(get("/api/tags/java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("java"));
    }

    @Test
    void shouldCreateTag() throws Exception {
        CreateTagRequest request = new CreateTagRequest();
        request.setName("New Tag");

        TagResponse response = new TagResponse();
        response.setId(1L);
        response.setName("New Tag");

        when(tagService.createTag(any(CreateTagRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/tags")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Tag"));
    }

    @Test
    void shouldDeleteTag() throws Exception {
        mockMvc.perform(delete("/api/tags/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(tagService).deleteTag(1L);
    }
}
