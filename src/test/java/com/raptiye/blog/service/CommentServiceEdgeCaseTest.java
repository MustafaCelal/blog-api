package com.raptiye.blog.service;

import com.raptiye.blog.domain.Comment;
import com.raptiye.blog.domain.Post;
import com.raptiye.blog.dto.request.CreateCommentRequest;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceEdgeCaseTest {

        @Mock
        private CommentRepository commentRepository;

        @Mock
        private PostRepository postRepository;

        @Mock
        private CommentMapper commentMapper;

        @InjectMocks
        private CommentService commentService;

        @Test
        void shouldThrowExceptionWhenCreatingCommentForNonExistentPost() {
                CreateCommentRequest request = CreateCommentRequest.builder()
                                .authorName("Author")
                                .authorEmail("author@test.com")
                                .content("Comment")
                                .build();

                when(postRepository.findById(999L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> commentService.createComment(999L, request))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessageContaining("Post not found");
        }

        @Test
        void shouldHandleEmptyCommentList() {
                when(commentRepository.findByPostIdAndApprovedTrueOrderByCreatedAtDesc(1L))
                                .thenReturn(Collections.emptyList());

                var result = commentService.getApprovedCommentsByPostId(1L);

                assertThat(result).isEmpty();
        }

        @Test
        void shouldHandleEmptyPendingCommentList() {
                when(commentRepository.findByApprovedFalseOrderByCreatedAtDesc())
                                .thenReturn(Collections.emptyList());

                var result = commentService.getPendingComments();

                assertThat(result).isEmpty();
        }

        @Test
        void shouldThrowExceptionWhenApprovingNonExistentComment() {
                when(commentRepository.findById(999L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> commentService.approveComment(999L))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessageContaining("Comment not found");
        }

        @Test
        void shouldThrowExceptionWhenDeletingNonExistentComment() {
                when(commentRepository.existsById(999L)).thenReturn(false);

                assertThatThrownBy(() -> commentService.deleteComment(999L))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessageContaining("Comment not found");
        }

        @Test
        void shouldHandleAlreadyApprovedComment() {
                Comment comment = Comment.builder()
                                .id(1L)
                                .approved(true)
                                .build();

                when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
                when(commentRepository.save(any(Comment.class))).thenReturn(comment);

                commentService.approveComment(1L);

                // Should still save even if already approved
                verify(commentRepository).save(comment);
                assertThat(comment.isApproved()).isTrue();
        }

        @Test
        void shouldCreateCommentWithMinimalData() {
                CreateCommentRequest request = CreateCommentRequest.builder()
                                .authorName("A")
                                .authorEmail("a@b.c")
                                .content("X")
                                .build();

                Post post = Post.builder().id(1L).build();
                Comment savedComment = Comment.builder().id(1L).build();

                when(postRepository.findById(1L)).thenReturn(Optional.of(post));
                when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

                commentService.createComment(1L, request);

                verify(commentRepository).save(any(Comment.class));
        }

        @Test
        void shouldHandleVeryLongCommentContent() {
                String longContent = "x".repeat(10000);

                CreateCommentRequest request = CreateCommentRequest.builder()
                                .authorName("Author")
                                .authorEmail("author@test.com")
                                .content(longContent)
                                .build();

                Post post = Post.builder().id(1L).build();
                Comment savedComment = Comment.builder().id(1L).content(longContent).build();

                when(postRepository.findById(1L)).thenReturn(Optional.of(post));
                when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

                assertThatCode(() -> commentService.createComment(1L, request))
                                .doesNotThrowAnyException();
        }
}
