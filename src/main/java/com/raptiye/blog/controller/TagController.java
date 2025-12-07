package com.raptiye.blog.controller;

import com.raptiye.blog.dto.request.CreateTagRequest;
import com.raptiye.blog.dto.response.TagResponse;
import com.raptiye.blog.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<TagResponse> getTagBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(tagService.getTagBySlug(slug));
    }

    @PostMapping
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody CreateTagRequest request) {
        TagResponse created = tagService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
