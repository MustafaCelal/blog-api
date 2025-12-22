package com.raptiye.blog.service;

import com.raptiye.blog.domain.Role;
import com.raptiye.blog.domain.User;
import com.raptiye.blog.dto.request.LoginRequest;
import com.raptiye.blog.dto.request.RegisterRequest;
import com.raptiye.blog.dto.response.AuthResponse;
import com.raptiye.blog.repository.UserRepository;
import com.raptiye.blog.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUser() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password");

        User.builder()
                .username("testuser")
                .role(Role.USER)
                .build();

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertThat(response.getToken()).isEqualTo("jwtToken");
        assertThat(response.getUsername()).isEqualTo("testuser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringExistingUsername() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existingUser");

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginUser() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        User user = User.builder()
                .username("testuser")
                .role(Role.USER)
                .build();

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("jwtToken");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertThat(response.getToken()).isEqualTo("jwtToken");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldThrowExceptionWhenLoginFails() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }
}
