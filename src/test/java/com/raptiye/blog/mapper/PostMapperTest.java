package com.raptiye.blog.mapper;

import com.raptiye.blog.domain.Comment;
import com.raptiye.blog.domain.Post;
import com.raptiye.blog.domain.Tag;
import com.raptiye.blog.dto.response.PostDetailResponse;
import com.raptiye.blog.dto.response.PostResponse;
import com.raptiye.blog.dto.response.TagResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostMapperTest {

    @Mock
    private TagMapper tagMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private PostMapper postMapper;

    private Post post;

    @BeforeEach
    void setUp() {
        Tag tag = Tag.builder().id(1L).name("Java").build();
        Comment comment = Comment.builder().id(1L).approved(true).build();

        post = Post.builder()
                .id(1L)
                .title("Title")
                .slug("title")
                .summary("Summary")
                .content("Content")
                .published(true)
                .tags(new HashSet<>(Set.of(tag)))
                .build();
        post.addComment(comment);
    }

    @Test
    void shouldReturnNullWhenPostIsNull() {
        assertThat(postMapper.toResponse(null)).isNull();
        assertThat(postMapper.toDetailResponse(null, true)).isNull();
    }

    @Test
    void shouldMapToResponse() {
        TagResponse tagResponse = new TagResponse();
        tagResponse.setName("Java");
        when(tagMapper.toResponseWithoutCount(any())).thenReturn(tagResponse);

        PostResponse response = postMapper.toResponse(post);

        assertThat(response.getTitle()).isEqualTo("Title");
        assertThat(response.getCommentCount()).isEqualTo(1);
        assertThat(response.getTags()).hasSize(1);
    }

    @Test
    void shouldMapToDetailResponseIncludingUnapproved() {
        Comment unapproved = Comment.builder().id(2L).approved(false).build();
        post.addComment(unapproved);

        TagResponse tagResponse = new TagResponse();
        when(tagMapper.toResponseWithoutCount(any())).thenReturn(tagResponse);

        PostDetailResponse response = postMapper.toDetailResponse(post, true);

        assertThat(response.getComments()).hasSize(2);
    }

    @Test
    void shouldMapToDetailResponseExcludingUnapproved() {
        Comment unapproved = Comment.builder().id(2L).approved(false).build();
        post.addComment(unapproved);

        TagResponse tagResponse = new TagResponse();
        when(tagMapper.toResponseWithoutCount(any())).thenReturn(tagResponse);

        PostDetailResponse response = postMapper.toDetailResponse(post, false);

        assertThat(response.getComments()).hasSize(1);
    }
}
