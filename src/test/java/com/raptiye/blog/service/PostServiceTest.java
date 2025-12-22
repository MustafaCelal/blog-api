package com.raptiye.blog.service;

import com.raptiye.blog.domain.Post;
import com.raptiye.blog.domain.Tag;
import com.raptiye.blog.dto.request.CreatePostRequest;
import com.raptiye.blog.dto.request.UpdatePostRequest;
import com.raptiye.blog.dto.response.PostDetailResponse;
import com.raptiye.blog.dto.response.PostResponse;
import com.raptiye.blog.exception.ResourceNotFoundException;
import com.raptiye.blog.mapper.PostMapper;
import com.raptiye.blog.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagService tagService;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    @Test
    void shouldCreatePost() {
        // Arrange
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("New Post");
        request.setContent("Post content");
        request.setSummary("Post summary");

        Post savedPost = Post.builder()
                .id(1L)
                .title("New Post")
                .content("Post content")
                .published(false)
                .build();
        savedPost.setCreatedAt(LocalDateTime.now());
        savedPost.setUpdatedAt(LocalDateTime.now());

        PostResponse response = new PostResponse();
        response.setId(1L);
        response.setTitle("New Post");

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        when(postMapper.toResponse(savedPost)).thenReturn(response);

        // Act
        PostResponse result = postService.createPost(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("New Post");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void shouldCreatePostWithTags() {
        // Arrange
        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("Tagged Post");
        request.setContent("Content");
        request.setTagIds(Set.of(1L));

        Tag tag = Tag.builder().id(1L).name("Java").build();

        Post savedPost = Post.builder()
                .id(1L)
                .title("Tagged Post")
                .tags(Set.of(tag))
                .build();

        PostResponse response = new PostResponse();
        response.setId(1L);

        when(tagService.getTagEntityById(1L)).thenReturn(tag);
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        when(postMapper.toResponse(savedPost)).thenReturn(response);

        // Act
        PostResponse result = postService.createPost(request);

        // Assert
        assertThat(result).isNotNull();
        verify(tagService).getTagEntityById(1L);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void shouldGetPublishedPosts() {
        // Arrange
        Post post = new Post();
        post.setId(1L);
        post.setPublished(true);

        PostResponse response = new PostResponse();
        response.setId(1L);

        when(postRepository.findByPublishedTrueOrderByCreatedAtDesc())
                .thenReturn(Collections.singletonList(post));
        when(postMapper.toResponse(post)).thenReturn(response);

        // Act
        var results = postService.getPublishedPosts();

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void shouldGetPostById() {
        // Arrange
        Long id = 1L;
        Post post = Post.builder().id(id).title("Test Post").build();
        PostDetailResponse response = new PostDetailResponse();
        response.setId(id);
        response.setTitle("Test Post");

        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        when(postMapper.toDetailResponse(post, false)).thenReturn(response);

        // Act
        PostDetailResponse result = postService.getPostById(id, false);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(postRepository).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenPostNotFoundById() {
        // Arrange
        Long id = 99L;
        when(postRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.getPostById(id, false));
    }

    @Test
    void shouldGetPostBySlug() {
        // Arrange
        String slug = "test-post";
        Post post = Post.builder().id(1L).slug(slug).build();
        PostDetailResponse response = new PostDetailResponse();
        response.setId(1L);
        response.setSlug(slug);

        when(postRepository.findBySlug(slug)).thenReturn(Optional.of(post));
        when(postMapper.toDetailResponse(post, false)).thenReturn(response);

        // Act
        PostDetailResponse result = postService.getPostBySlug(slug, false);

        // Assert
        assertThat(result.getSlug()).isEqualTo(slug);
        verify(postRepository).findBySlug(slug);
    }

    @Test
    void shouldThrowExceptionWhenPostNotFoundBySlug() {
        // Arrange
        String slug = "non-existent";
        when(postRepository.findBySlug(slug)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.getPostBySlug(slug, false));
    }

    @Test
    void shouldUpdatePost() {
        // Arrange
        Long id = 1L;
        UpdatePostRequest request = new UpdatePostRequest();
        request.setTitle("Updated Title");

        Post existingPost = Post.builder().id(id).title("Old Title").build();
        Post updatedPost = Post.builder().id(id).title("Updated Title").build();
        PostResponse response = new PostResponse();
        response.setId(id);
        response.setTitle("Updated Title");

        when(postRepository.findById(id)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenReturn(updatedPost);
        when(postMapper.toResponse(updatedPost)).thenReturn(response);

        // Act
        PostResponse result = postService.updatePost(id, request);

        // Assert
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void shouldDeletePost() {
        // Arrange
        Long id = 1L;
        when(postRepository.existsById(id)).thenReturn(true);

        // Act
        postService.deletePost(id);

        // Assert
        verify(postRepository).deleteById(id);
    }

    @Test
    void shouldAddTagToPost() {
        // Arrange
        Long postId = 1L;
        Long tagId = 2L;
        Post post = Post.builder().id(postId).title("Post").build();
        Tag tag = Tag.builder().id(tagId).name("Java").build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(tagService.getTagEntityById(tagId)).thenReturn(tag);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(new PostResponse());

        // Act
        postService.addTagToPost(postId, tagId);

        // Assert
        verify(postRepository).save(post);
        assertThat(post.getTags()).contains(tag);
    }

    @Test
    void shouldRemoveTagFromPost() {
        // Arrange
        Long postId = 1L;
        Long tagId = 2L;
        Tag tag = Tag.builder().id(tagId).name("Java").build();
        Post post = Post.builder().id(postId).title("Post").build();
        post.addTag(tag);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(tagService.getTagEntityById(tagId)).thenReturn(tag);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(new PostResponse());

        // Act
        postService.removeTagFromPost(postId, tagId);

        // Assert
        verify(postRepository).save(post);
        assertThat(post.getTags()).doesNotContain(tag);
    }

    @Test
    void shouldGetPublishedPostSnippets() {
        // Arrange
        PostResponse response = new PostResponse();
        response.setId(1L);

        when(postRepository.findPublishedPostResponsesWithSnippet(350))
                .thenReturn(List.of(response));

        // Act
        List<PostResponse> results = postService.getPublishedPostSnippets();

        // Assert
        assertThat(results).hasSize(1);
    }
}
