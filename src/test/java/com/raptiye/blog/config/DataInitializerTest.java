package com.raptiye.blog.config;

import com.raptiye.blog.repository.CommentRepository;
import com.raptiye.blog.repository.PostRepository;
import com.raptiye.blog.repository.TagRepository;
import com.raptiye.blog.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void shouldSkipInitializationIfPasswordNotSet() {
        ReflectionTestUtils.setField(dataInitializer, "adminPassword", null);

        dataInitializer.run();

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldInitializeDataIfEmpty() {
        ReflectionTestUtils.setField(dataInitializer, "adminPassword", "password");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(postRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(tagRepository.save(any())).thenAnswer(invocation -> {
            com.raptiye.blog.domain.Tag tag = invocation.getArgument(0);
            tag.setId(1L);
            return tag;
        });
        when(postRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        dataInitializer.run();

        verify(userRepository, atLeastOnce()).save(any());
        verify(tagRepository, atLeastOnce()).save(any());
        verify(postRepository, atLeastOnce()).save(any());
        verify(commentRepository, atLeastOnce()).save(any());
    }

    @Test
    void shouldSkipBlogDataIfAlreadyExists() {
        ReflectionTestUtils.setField(dataInitializer, "adminPassword", "password");
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        when(postRepository.count()).thenReturn(5L);

        dataInitializer.run();

        verify(postRepository, never()).save(any());
    }
}
