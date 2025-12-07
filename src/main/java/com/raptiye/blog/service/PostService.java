package com.raptiye.blog.service;

import com.raptiye.blog.domain.Post;
import com.raptiye.blog.domain.Tag;
import com.raptiye.blog.dto.request.CreatePostRequest;
import com.raptiye.blog.dto.request.UpdatePostRequest;
import com.raptiye.blog.dto.response.PostDetailResponse;
import com.raptiye.blog.dto.response.PostResponse;
import com.raptiye.blog.exception.ResourceNotFoundException;
import com.raptiye.blog.mapper.PostMapper;
import com.raptiye.blog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final TagService tagService;
    private final PostMapper postMapper;

    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getPublishedPosts() {
        return postRepository.findByPublishedTrueOrderByCreatedAtDesc().stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    public PostDetailResponse getPostBySlug(String slug, boolean includeUnapprovedComments) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "slug", slug));
        return postMapper.toDetailResponse(post, includeUnapprovedComments);
    }

    public PostDetailResponse getPostById(Long id, boolean includeUnapprovedComments) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return postMapper.toDetailResponse(post, includeUnapprovedComments);
    }

    public List<PostResponse> getPostsByTagSlug(String tagSlug) {
        return postRepository.findByTagSlug(tagSlug).stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PostResponse createPost(CreatePostRequest request) {
        Post post = Post.builder()
                .title(request.getTitle())
                .summary(request.getSummary())
                .content(request.getContent())
                .published(false)
                .build();

        // Add tags if provided
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = request.getTagIds().stream()
                    .map(tagService::getTagEntityById)
                    .collect(Collectors.toSet());
            tags.forEach(post::addTag);
        }

        Post savedPost = postRepository.save(post);
        return postMapper.toResponse(savedPost);
    }

    @Transactional
    public PostResponse updatePost(Long id, UpdatePostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getSummary() != null) {
            post.setSummary(request.getSummary());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getPublished() != null) {
            post.setPublished(request.getPublished());
        }

        Post savedPost = postRepository.save(post);
        return postMapper.toResponse(savedPost);
    }

    @Transactional
    public PostResponse addTagToPost(Long postId, Long tagId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Tag tag = tagService.getTagEntityById(tagId);
        post.addTag(tag);

        Post savedPost = postRepository.save(post);
        return postMapper.toResponse(savedPost);
    }

    @Transactional
    public PostResponse removeTagFromPost(Long postId, Long tagId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Tag tag = tagService.getTagEntityById(tagId);
        post.removeTag(tag);

        Post savedPost = postRepository.save(post);
        return postMapper.toResponse(savedPost);
    }

    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post", "id", id);
        }
        postRepository.deleteById(id);
    }
}
