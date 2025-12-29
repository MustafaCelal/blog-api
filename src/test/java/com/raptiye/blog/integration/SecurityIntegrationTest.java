package com.raptiye.blog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raptiye.blog.dto.request.LoginRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void shouldRegisterAndLoginSuccessfully() throws Exception {
                // Register
                RegisterRequest registerRequest = new RegisterRequest();
                registerRequest.setUsername("securitytest" + System.currentTimeMillis());
                registerRequest.setEmail("security" + System.currentTimeMillis() + "@test.com");
                registerRequest.setPassword("password123");

                MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.token").exists())
                                .andExpect(jsonPath("$.username").value(registerRequest.getUsername()))
                                .andReturn();

                AuthResponse registerResponse = objectMapper.readValue(
                                registerResult.getResponse().getContentAsString(),
                                AuthResponse.class);

                assertThat(registerResponse.getToken()).isNotEmpty();
                assertThat(registerResponse.getRole()).isEqualTo("USER");

                // Login with same credentials
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setUsername(registerRequest.getUsername());
                loginRequest.setPassword(registerRequest.getPassword());

                MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").exists())
                                .andReturn();

                AuthResponse loginResponse = objectMapper.readValue(
                                loginResult.getResponse().getContentAsString(),
                                AuthResponse.class);

                assertThat(loginResponse.getToken()).isNotEmpty();
        }

        @Test
        void shouldRejectInvalidCredentials() throws Exception {
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setUsername("nonexistent");
                loginRequest.setPassword("wrongpassword");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldRejectDuplicateUsername() throws Exception {
                String uniqueUsername = "duplicate" + System.currentTimeMillis();

                RegisterRequest request1 = new RegisterRequest();
                request1.setUsername(uniqueUsername);
                request1.setEmail("first@test.com");
                request1.setPassword("password123");

                // First registration should succeed
                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request1)))
                                .andExpect(status().isCreated());

                // Second registration with same username should fail
                RegisterRequest request2 = new RegisterRequest();
                request2.setUsername(uniqueUsername);
                request2.setEmail("second@test.com");
                request2.setPassword("password123");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request2)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldProtectAdminEndpoints() throws Exception {
                // Try to delete a tag without authentication
                mockMvc.perform(delete("/api/tags/1"))
                                .andExpect(status().isUnauthorized()); // Returns 401 because not authenticated

                // Try to create a post without authentication
                mockMvc.perform(post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isUnauthorized());

                // Try to approve a comment without authentication
                mockMvc.perform(put("/api/comments/1/approve"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldAllowPublicEndpoints() throws Exception {
                // Public endpoints should work without authentication
                mockMvc.perform(get("/api/posts"))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/api/tags"))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/"))
                                .andExpect(status().isOk());
        }

        @Test
        void shouldValidateRegistrationInput() throws Exception {
                // Invalid email
                RegisterRequest invalidEmail = new RegisterRequest();
                invalidEmail.setUsername("testuser");
                invalidEmail.setEmail("invalid-email");
                invalidEmail.setPassword("password123");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidEmail)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.validationErrors.email").exists());

                // Short password
                RegisterRequest shortPassword = new RegisterRequest();
                shortPassword.setUsername("testuser2");
                shortPassword.setEmail("test@test.com");
                shortPassword.setPassword("123");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(shortPassword)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.validationErrors.password").exists());

                // Short username
                RegisterRequest shortUsername = new RegisterRequest();
                shortUsername.setUsername("ab");
                shortUsername.setEmail("test2@test.com");
                shortUsername.setPassword("password123");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(shortUsername)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.validationErrors.username").exists());
        }
}
