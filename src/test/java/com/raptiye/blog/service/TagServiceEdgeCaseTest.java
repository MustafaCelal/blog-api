package com.raptiye.blog.service;

import com.raptiye.blog.domain.Tag;
import com.raptiye.blog.dto.request.CreateTagRequest;
import com.raptiye.blog.exception.ResourceNotFoundException;
import com.raptiye.blog.mapper.TagMapper;
import com.raptiye.blog.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceEdgeCaseTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagService tagService;

    @Test
    void shouldThrowExceptionWhenCreatingDuplicateTagName() {
        CreateTagRequest request = new CreateTagRequest("Existing");

        when(tagRepository.existsByName("Existing")).thenReturn(true);

        assertThatThrownBy(() -> tagService.createTag(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void shouldThrowExceptionWhenGettingNonExistentTagBySlug() {
        when(tagRepository.findBySlug("non-existent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.getTagBySlug("non-existent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tag not found");
    }

    @Test
    void shouldThrowExceptionWhenGettingNonExistentTagById() {
        when(tagRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.getTagEntityById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tag not found");
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTag() {
        when(tagRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> tagService.deleteTag(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tag not found");
    }

    @Test
    void shouldHandleEmptyTagList() {
        when(tagRepository.findAll()).thenReturn(Collections.emptyList());

        var result = tagService.getAllTags();

        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleTagWithSpecialCharacters() {
        CreateTagRequest request = new CreateTagRequest("C++ & Java!");

        when(tagRepository.existsByName(any())).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> {
            Tag tag = invocation.getArgument(0);
            tag.setId(1L);
            return tag;
        });

        tagService.createTag(request);

        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void shouldHandleVeryLongTagName() {
        String longName = "A".repeat(100);
        CreateTagRequest request = new CreateTagRequest(longName);

        when(tagRepository.existsByName(any())).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> {
            Tag tag = invocation.getArgument(0);
            tag.setId(1L);
            return tag;
        });

        assertThatCode(() -> tagService.createTag(request))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldHandleTagNameWithOnlySpaces() {
        CreateTagRequest request = new CreateTagRequest("   Spaces   ");

        when(tagRepository.existsByName(any())).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> {
            Tag tag = invocation.getArgument(0);
            tag.setId(1L);
            return tag;
        });

        tagService.createTag(request);

        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void shouldDeleteTagWithPosts() {
        when(tagRepository.existsById(1L)).thenReturn(true);

        tagService.deleteTag(1L);

        verify(tagRepository).deleteById(1L);
    }
}
