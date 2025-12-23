package com.raptiye.blog.mapper;

import com.raptiye.blog.domain.Comment;
import com.raptiye.blog.domain.Tag;
import com.raptiye.blog.dto.response.CommentResponse;
import com.raptiye.blog.dto.response.TagResponse;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class GeneralMapperTest {

    private final TagMapper tagMapper = new TagMapper();
    private final CommentMapper commentMapper = new CommentMapper();

    @Test
    void tagMapperShouldHandleNull() {
        assertThat(tagMapper.toResponse(null)).isNull();
        assertThat(tagMapper.toResponseWithoutCount(null)).isNull();
    }

    @Test
    void tagMapperShouldMapFields() {
        Tag tag = Tag.builder().id(1L).name("Java").slug("java").posts(Collections.emptySet()).build();
        TagResponse response = tagMapper.toResponse(tag);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Java");
        assertThat(response.getPostCount()).isEqualTo(0);

        TagResponse response2 = tagMapper.toResponseWithoutCount(tag);
        assertThat(response2.getPostCount()).isEqualTo(0);
    }

    @Test
    void commentMapperShouldHandleNull() {
        assertThat(commentMapper.toResponse(null)).isNull();
    }

    @Test
    void commentMapperShouldMapFields() {
        Comment comment = Comment.builder().id(1L).authorName("User").content("Text").approved(true).build();
        CommentResponse response = commentMapper.toResponse(comment);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getAuthorName()).isEqualTo("User");
        assertThat(response.isApproved()).isTrue();
    }
}
