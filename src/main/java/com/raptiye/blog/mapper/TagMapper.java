package com.raptiye.blog.mapper;

import com.raptiye.blog.domain.Tag;
import com.raptiye.blog.dto.response.TagResponse;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public TagResponse toResponse(Tag tag) {
        if (tag == null) {
            return null;
        }
        
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .postCount(tag.getPosts() != null ? tag.getPosts().size() : 0)
                .createdAt(tag.getCreatedAt())
                .build();
    }

    public TagResponse toResponseWithoutCount(Tag tag) {
        if (tag == null) {
            return null;
        }
        
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .createdAt(tag.getCreatedAt())
                .build();
    }
}
