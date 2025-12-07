package com.raptiye.blog.mapper;

import com.raptiye.blog.domain.Comment;
import com.raptiye.blog.dto.response.CommentResponse;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentResponse toResponse(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .authorName(comment.getAuthorName())
                .content(comment.getContent())
                .approved(comment.isApproved())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
