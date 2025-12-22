package com.raptiye.blog.service;

import com.raptiye.blog.domain.Tag;
import com.raptiye.blog.dto.request.CreateTagRequest;
import com.raptiye.blog.dto.response.TagResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagService tagService;

    @Test
    void shouldGetAllTags() {
        // Arrange
        Tag tag = Tag.builder().id(1L).name("Java").build();
        TagResponse response = new TagResponse();
        response.setId(1L);
        response.setName("Java");

        when(tagRepository.findAll()).thenReturn(Collections.singletonList(tag));
        when(tagMapper.toResponse(tag)).thenReturn(response);

        // Act
        var results = tagService.getAllTags();

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Java");
    }

    @Test
    void shouldGetTagBySlug() {
        // Arrange
        String slug = "java";
        Tag tag = Tag.builder().id(1L).name("Java").slug(slug).build();
        TagResponse response = new TagResponse();
        response.setId(1L);
        response.setSlug(slug);

        when(tagRepository.findBySlug(slug)).thenReturn(Optional.of(tag));
        when(tagMapper.toResponse(tag)).thenReturn(response);

        // Act
        TagResponse result = tagService.getTagBySlug(slug);

        // Assert
        assertThat(result.getSlug()).isEqualTo(slug);
    }

    @Test
    void shouldThrowExceptionWhenTagNotFoundBySlug() {
        // Arrange
        String slug = "non-existent";
        when(tagRepository.findBySlug(slug)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> tagService.getTagBySlug(slug));
    }

    @Test
    void shouldGetTagById() {
        // Arrange
        Long id = 1L;
        Tag tag = Tag.builder().id(id).name("Java").build();
        TagResponse response = new TagResponse();
        response.setId(id);

        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));
        when(tagMapper.toResponse(tag)).thenReturn(response);

        // Act
        TagResponse result = tagService.getTagById(id);

        // Assert
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void shouldThrowExceptionWhenTagNotFoundById() {
        // Arrange
        Long id = 99L;
        when(tagRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> tagService.getTagById(id));
    }

    @Test
    void shouldCreateTag() {
        // Arrange
        CreateTagRequest request = new CreateTagRequest();
        request.setName("Spring Boot");

        Tag.builder().name("Spring Boot").build();
        Tag savedTag = Tag.builder().id(1L).name("Spring Boot").build();
        TagResponse response = new TagResponse();
        response.setId(1L);
        response.setName("Spring Boot");

        when(tagRepository.existsByName(request.getName())).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);
        when(tagMapper.toResponse(savedTag)).thenReturn(response);

        // Act
        TagResponse result = tagService.createTag(request);

        // Assert
        assertThat(result.getName()).isEqualTo("Spring Boot");
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingDuplicateTag() {
        // Arrange
        CreateTagRequest request = new CreateTagRequest();
        request.setName("Java");

        when(tagRepository.existsByName(request.getName())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tagService.createTag(request));
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void shouldDeleteTag() {
        // Arrange
        Long id = 1L;
        when(tagRepository.existsById(id)).thenReturn(true);

        // Act
        tagService.deleteTag(id);

        // Assert
        verify(tagRepository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTag() {
        // Arrange
        Long id = 99L;
        when(tagRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> tagService.deleteTag(id));
        verify(tagRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldGetTagEntityById() {
        // Arrange
        Long id = 1L;
        Tag tag = Tag.builder().id(id).name("Java").build();
        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));

        // Act
        Tag result = tagService.getTagEntityById(id);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void shouldThrowExceptionWhenTagEntityNotFoundById() {
        // Arrange
        Long id = 1L;
        when(tagRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> tagService.getTagEntityById(id));
    }
}
