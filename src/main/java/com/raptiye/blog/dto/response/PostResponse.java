package com.raptiye.blog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {

    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private boolean published;
    private List<TagResponse> tags;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostResponse(Long id, String title, String content, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content; // Kısaltılmış metin buraya gelir
        this.createdAt = createdAt;
    }
}
