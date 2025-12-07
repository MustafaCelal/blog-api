package com.raptiye.blog.service;

import com.raptiye.blog.domain.Comment;
import com.raptiye.blog.domain.Post;
import com.raptiye.blog.dto.request.CreateCommentRequest;
import com.raptiye.blog.dto.response.CommentResponse;
import com.raptiye.blog.exception.ResourceNotFoundException;
import com.raptiye.blog.mapper.CommentMapper;
import com.raptiye.blog.repository.CommentRepository;
import com.raptiye.blog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

    public List<CommentResponse> getApprovedCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdAndApprovedTrueOrderByCreatedAtDesc(postId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<CommentResponse> getAllCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<CommentResponse> getPendingComments() {
        return commentRepository.findByApprovedFalseOrderByCreatedAtDesc().stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse createComment(Long postId, CreateCommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Comment comment = Comment.builder()
                .authorName(request.getAuthorName())
                .authorEmail(request.getAuthorEmail())
                .content(request.getContent())
                .approved(false)
                .post(post)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toResponse(savedComment);
    }

    @Transactional
    public CommentResponse approveComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        comment.setApproved(true);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toResponse(savedComment);
    }

    @Transactional
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comment", "id", id);
        }
        commentRepository.deleteById(id);
    }
}
