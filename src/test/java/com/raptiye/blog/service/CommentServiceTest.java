package com.raptiye.blog.service;

import com.raptiye.blog.domain.Comment;
import com.raptiye.blog.domain.Post;
import com.raptiye.blog.dto.request.CreateCommentRequest;
import com.raptiye.blog.dto.response.CommentResponse;
import com.raptiye.blog.exception.ResourceNotFoundException;
import com.raptiye.blog.mapper.CommentMapper;
import com.raptiye.blog.repository.CommentRepository;
import com.raptiye.blog.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @Test
    void shouldGetApprovedCommentsByPostId() {
        // Arrange
        Long postId = 1L;
        Comment comment = Comment.builder().id(1L).approved(true).build();
        CommentResponse response = new CommentResponse();
        response.setId(1L);

        when(commentRepository.findByPostIdAndApprovedTrueOrderByCreatedAtDesc(postId))
                .thenReturn(Collections.singletonList(comment));
        when(commentMapper.toResponse(comment)).thenReturn(response);

        // Act
        List<CommentResponse> results = commentService.getApprovedCommentsByPostId(postId);

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void shouldCreateComment() {
        // Arrange
        Long postId = 1L;
        CreateCommentRequest request = new CreateCommentRequest();
        request.setAuthorName("John");
        request.setAuthorEmail("john@example.com");
        request.setContent("Nice post");

        Post post = Post.builder().id(postId).build();
        Comment comment = Comment.builder().id(1L).authorName("John").build();
        CommentResponse response = new CommentResponse();
        response.setId(1L);
        response.setAuthorName("John");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponse(comment)).thenReturn(response);

        // Act
        CommentResponse result = commentService.createComment(postId, request);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAuthorName()).isEqualTo("John");
        verify(postRepository).findById(postId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingCommentForNonExistentPost() {
        // Arrange
        Long postId = 99L;
        CreateCommentRequest request = new CreateCommentRequest();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(postId, request));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void shouldApproveComment() {
        // Arrange
        Long commentId = 1L;
        Comment comment = Comment.builder().id(commentId).approved(false).build();
        Comment approvedComment = Comment.builder().id(commentId).approved(true).build();
        CommentResponse response = new CommentResponse();
        response.setId(commentId);
        response.setApproved(true);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(approvedComment);
        when(commentMapper.toResponse(approvedComment)).thenReturn(response);

        // Act
        CommentResponse result = commentService.approveComment(commentId);

        // Assert
        assertThat(result.isApproved()).isTrue();
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void shouldDeleteComment() {
        // Arrange
        Long commentId = 1L;
        when(commentRepository.existsById(commentId)).thenReturn(true);

        // Act
        commentService.deleteComment(commentId);

        // Assert
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void shouldGetPendingComments() {
        // Arrange
        Comment comment = Comment.builder().id(1L).approved(false).build();
        CommentResponse response = new CommentResponse();
        response.setId(1L);
        response.setApproved(false);

        when(commentRepository.findByApprovedFalseOrderByCreatedAtDesc())
                .thenReturn(Collections.singletonList(comment));
        when(commentMapper.toResponse(comment)).thenReturn(response);

        // Act
        List<CommentResponse> results = commentService.getPendingComments();

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).isApproved()).isFalse();
    }

    @Test
    void shouldGetAllCommentsByPostId() {
        // Arrange
        Long postId = 1L;
        Comment comment = Comment.builder().id(1L).build();
        CommentResponse response = new CommentResponse();
        response.setId(1L);

        when(commentRepository.findByPostIdOrderByCreatedAtDesc(postId))
                .thenReturn(Collections.singletonList(comment));
        when(commentMapper.toResponse(comment)).thenReturn(response);

        // Act
        List<CommentResponse> results = commentService.getAllCommentsByPostId(postId);

        // Assert
        assertThat(results).hasSize(1);
    }
}
