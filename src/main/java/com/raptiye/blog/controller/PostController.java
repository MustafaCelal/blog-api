package com.raptiye.blog.controller;

import com.raptiye.blog.dto.request.CreatePostRequest;
import com.raptiye.blog.dto.request.UpdatePostRequest;
import com.raptiye.blog.dto.response.PostDetailResponse;
import com.raptiye.blog.dto.response.PostResponse;
import com.raptiye.blog.service.PostService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "true") boolean publishedOnly) {

        if (publishedOnly) {
            return ResponseEntity.ok(postService.getPublishedPosts());
        }
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PostDetailResponse> getPostBySlug(
            @PathVariable String slug,
            @RequestParam(defaultValue = "false") boolean includeAllComments) {

        return ResponseEntity.ok(postService.getPostBySlug(slug, includeAllComments));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PostDetailResponse> getPostById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean includeAllComments) {

        return ResponseEntity.ok(postService.getPostById(id, includeAllComments));
    }

    @GetMapping("/tag/{tagSlug}")
    public ResponseEntity<List<PostResponse>> getPostsByTag(@PathVariable String tagSlug) {
        return ResponseEntity.ok(postService.getPostsByTagSlug(tagSlug));
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        PostResponse created = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePostRequest request) {

        return ResponseEntity.ok(postService.updatePost(id, request));
    }

    @PostMapping("/{postId}/tags/{tagId}")
    public ResponseEntity<PostResponse> addTagToPost(
            @PathVariable Long postId,
            @PathVariable Long tagId) {

        return ResponseEntity.ok(postService.addTagToPost(postId, tagId));
    }

    @DeleteMapping("/{postId}/tags/{tagId}")
    public ResponseEntity<PostResponse> removeTagFromPost(
            @PathVariable Long postId,
            @PathVariable Long tagId) {

        return ResponseEntity.ok(postService.removeTagFromPost(postId, tagId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
