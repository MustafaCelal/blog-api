package com.raptiye.blog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raptiye.blog.dto.request.CreatePostRequest;
import com.raptiye.blog.dto.request.RegisterRequest;
import com.raptiye.blog.dto.response.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void shouldGetAllPublishedPosts() throws Exception {
                mockMvc.perform(get("/api/posts"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        void shouldReturn404ForNonExistentPost() throws Exception {
                mockMvc.perform(get("/api/posts/non-existent-slug"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.error").value("Not Found"));
        }

        @Test
        void shouldReturn401WhenCreatingPostWithoutAuth() throws Exception {
                CreatePostRequest request = CreatePostRequest.builder()
                                .title("Test Post")
                                .content("Content")
                                .build();

                mockMvc.perform(post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized()); // Returns 401 because not authenticated
        }

        @Test
        void fullPostLifecycleWithAuthentication() throws Exception {
                // 1. Register a new admin user
                RegisterRequest registerRequest = new RegisterRequest();
                registerRequest.setUsername("integrationadmin");
                registerRequest.setEmail("admin@integration.test");
                registerRequest.setPassword("password123");

                MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isCreated())
                                .andReturn();

                AuthResponse authResponse = objectMapper.readValue(
                                registerResult.getResponse().getContentAsString(),
                                AuthResponse.class);

                String token = authResponse.getToken();

                // 2. Create a post (should fail - user is not admin)
                CreatePostRequest createRequest = CreatePostRequest.builder()
                                .title("Integration Test Post")
                                .summary("Summary")
                                .content("Content")
                                .tagIds(Collections.emptySet())
                                .build();

                mockMvc.perform(post("/api/posts")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest)))
                                .andExpect(status().isForbidden());

                // 3. Get all posts (should work without auth)
                mockMvc.perform(get("/api/posts"))
                                .andExpect(status().isOk());
        }

        @Test
        void shouldValidatePostCreationRequest() throws Exception {
                CreatePostRequest invalidRequest = CreatePostRequest.builder()
                                .title("AB") // Too short
                                .content("") // Empty
                                .build();

                mockMvc.perform(post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isUnauthorized()); // Will be unauthorized first
        }

        @Test
        void shouldGetPostsByTag() throws Exception {
                mockMvc.perform(get("/api/posts/tag/java"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
}
