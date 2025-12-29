package com.raptiye.blog.service;

import com.raptiye.blog.domain.Post;
import com.raptiye.blog.dto.request.CreatePostRequest;
import com.raptiye.blog.dto.request.UpdatePostRequest;
import com.raptiye.blog.exception.ResourceNotFoundException;
import com.raptiye.blog.mapper.PostMapper;
import com.raptiye.blog.repository.PostRepository;
import com.raptiye.blog.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceEdgeCaseTest {

        @Mock
        private PostRepository postRepository;

        @Mock
        private TagRepository tagRepository;

        @Mock
        private PostMapper postMapper;

        @Mock
        private TagService tagService;

        @InjectMocks
        private PostService postService;

        @Test
        void shouldHandleNullTagIdsInCreatePost() {
                CreatePostRequest request = CreatePostRequest.builder()
                                .title("Test")
                                .content("Content")
                                .tagIds(null) // Null tags
                                .build();

                Post savedPost = Post.builder().id(1L).title("Test").build();
                when(postRepository.save(any(Post.class))).thenReturn(savedPost);

                postService.createPost(request);

                verify(postRepository).save(any(Post.class));
                verify(tagService, never()).getTagEntityById(anyLong());
        }

        @Test
        void shouldHandleEmptyTagIdsInCreatePost() {
                CreatePostRequest request = CreatePostRequest.builder()
                                .title("Test")
                                .content("Content")
                                .tagIds(Collections.emptySet()) // Empty tags
                                .build();

                Post savedPost = Post.builder().id(1L).title("Test").build();
                when(postRepository.save(any(Post.class))).thenReturn(savedPost);

                postService.createPost(request);

                verify(postRepository).save(any(Post.class));
                verify(tagService, never()).getTagEntityById(anyLong());
        }

        @Test
        void shouldThrowExceptionWhenUpdatingNonExistentPost() {
                UpdatePostRequest request = new UpdatePostRequest();
                request.setTitle("Updated");

                when(postRepository.findById(999L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> postService.updatePost(999L, request))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessageContaining("Post not found");
        }

        @Test
        void shouldHandleNullFieldsInUpdatePost() {
                Post existingPost = Post.builder()
                                .id(1L)
                                .title("Original")
                                .summary("Original Summary")
                                .content("Original Content")
                                .published(false)
                                .build();

                UpdatePostRequest request = new UpdatePostRequest();
                // All fields are null

                when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
                when(postRepository.save(any(Post.class))).thenReturn(existingPost);

                postService.updatePost(1L, request);

                // Original values should be preserved
                assertThat(existingPost.getTitle()).isEqualTo("Original");
                assertThat(existingPost.getSummary()).isEqualTo("Original Summary");
                assertThat(existingPost.getContent()).isEqualTo("Original Content");
                assertThat(existingPost.isPublished()).isFalse();
        }

        @Test
        void shouldHandlePartialUpdatePost() {
                Post existingPost = Post.builder()
                                .id(1L)
                                .title("Original")
                                .summary("Original Summary")
                                .content("Original Content")
                                .published(false)
                                .build();

                UpdatePostRequest request = new UpdatePostRequest();
                request.setTitle("Updated Title");
                // Other fields are null

                when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
                when(postRepository.save(any(Post.class))).thenReturn(existingPost);

                postService.updatePost(1L, request);

                assertThat(existingPost.getTitle()).isEqualTo("Updated Title");
                assertThat(existingPost.getSummary()).isEqualTo("Original Summary");
                assertThat(existingPost.getContent()).isEqualTo("Original Content");
        }

        @Test
        void shouldThrowExceptionWhenDeletingNonExistentPost() {
                when(postRepository.existsById(999L)).thenReturn(false);

                assertThatThrownBy(() -> postService.deletePost(999L))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessageContaining("Post not found");
        }

        @Test
        void shouldThrowExceptionWhenGettingNonExistentPostBySlug() {
                when(postRepository.findBySlug("non-existent")).thenReturn(Optional.empty());

                assertThatThrownBy(() -> postService.getPostBySlug("non-existent", false))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessageContaining("Post not found");
        }

        @Test
        void shouldThrowExceptionWhenGettingNonExistentPostById() {
                when(postRepository.findById(999L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> postService.getPostById(999L, false))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessageContaining("Post not found");
        }

        @Test
        void shouldThrowExceptionWhenAddingNonExistentTagToPost() {
                Post post = Post.builder().id(1L).build();
                when(postRepository.findById(1L)).thenReturn(Optional.of(post));
                when(tagService.getTagEntityById(999L))
                                .thenThrow(new ResourceNotFoundException("Tag", "id", 999L));

                assertThatThrownBy(() -> postService.addTagToPost(1L, 999L))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessageContaining("Tag not found");
        }

        @Test
        void shouldThrowExceptionWhenRemovingNonExistentTagFromPost() {
                Post post = Post.builder().id(1L).tags(new HashSet<>()).build();
                when(postRepository.findById(1L)).thenReturn(Optional.of(post));
                when(tagService.getTagEntityById(999L))
                                .thenThrow(new ResourceNotFoundException("Tag", "id", 999L));

                assertThatThrownBy(() -> postService.removeTagFromPost(1L, 999L))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessageContaining("Tag not found");
        }

        @Test
        void shouldHandleEmptyPostList() {
                when(postRepository.findByPublishedTrueOrderByCreatedAtDesc())
                                .thenReturn(Collections.emptyList());

                var result = postService.getPublishedPosts();

                assertThat(result).isEmpty();
        }

        @Test
        void shouldHandleEmptyTagFilterResult() {
                when(postRepository.findByTagSlug("non-existent-tag"))
                                .thenReturn(Collections.emptyList());

                var result = postService.getPostsByTagSlug("non-existent-tag");

                assertThat(result).isEmpty();
        }

        @Test
        void shouldHandlePostWithNoTags() {
                Post post = Post.builder()
                                .id(1L)
                                .title("No Tags")
                                .tags(new HashSet<>())
                                .comments(new ArrayList<>())
                                .build();

                when(postRepository.findById(1L)).thenReturn(Optional.of(post));

                // Should not throw exception
                assertThatCode(() -> postService.getPostById(1L, false))
                                .doesNotThrowAnyException();
        }

        @Test
        void shouldHandlePostWithNoComments() {
                Post post = Post.builder()
                                .id(1L)
                                .title("No Comments")
                                .tags(new HashSet<>())
                                .comments(new ArrayList<>())
                                .build();

                when(postRepository.findById(1L)).thenReturn(Optional.of(post));

                assertThatCode(() -> postService.getPostById(1L, false))
                                .doesNotThrowAnyException();
        }
}
