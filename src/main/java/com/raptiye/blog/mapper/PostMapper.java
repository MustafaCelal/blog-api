package com.raptiye.blog.mapper;

import com.raptiye.blog.domain.Post;
import com.raptiye.blog.dto.response.PostDetailResponse;
import com.raptiye.blog.dto.response.PostResponse;
import com.raptiye.blog.dto.response.TagResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final TagMapper tagMapper;
    private final CommentMapper commentMapper;

    public PostResponse toResponse(Post post) {
        if (post == null) {
            return null;
        }

        List<TagResponse> tags = post.getTags().stream()
                .map(tagMapper::toResponseWithoutCount)
                .collect(Collectors.toList());

        int commentCount = (int) post.getComments().stream()
                .filter(comment -> comment.isApproved())
                .count();

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .summary(post.getSummary())
                .content(post.getContent())
                .published(post.isPublished())
                .tags(tags)
                .commentCount(commentCount)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public PostDetailResponse toDetailResponse(Post post, boolean includeUnapprovedComments) {
        if (post == null) {
            return null;
        }

        List<TagResponse> tags = post.getTags().stream()
                .map(tagMapper::toResponseWithoutCount)
                .collect(Collectors.toList());

        var commentsStream = post.getComments().stream();
        if (!includeUnapprovedComments) {
            commentsStream = commentsStream.filter(comment -> comment.isApproved());
        }

        var comments = commentsStream
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());

        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .summary(post.getSummary())
                .content(post.getContent())
                .published(post.isPublished())
                .tags(tags)
                .comments(comments)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
