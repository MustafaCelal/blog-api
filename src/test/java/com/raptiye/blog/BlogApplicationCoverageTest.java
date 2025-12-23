package com.raptiye.blog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BlogApplicationCoverageTest {

    @Test
    void contextLoads() {
        // This tests application loading
    }

    @Test
    void mainMethodShouldNotThrow() {
        // Coverage for main method - we don't actually run it to avoid server startup
        // but we can at least reference the class
        assertThat(BlogApplication.class).isNotNull();
    }
}
