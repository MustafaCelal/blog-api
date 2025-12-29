package com.raptiye.blog.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"; // Mock secret
                                                                                                      // (Base64)
    private final long expiration = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", expiration);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());

        String token = jwtUtil.generateToken(userDetails);

        assertThat(token).isNotNull();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("testuser");
        assertThat(jwtUtil.validateToken(token, userDetails)).isTrue();
    }

    @Test
    void shouldExtractExpiration() {
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        assertThat(jwtUtil.extractExpiration(token)).isInTheFuture();
    }
}
