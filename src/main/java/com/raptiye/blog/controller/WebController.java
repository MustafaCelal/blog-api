package com.raptiye.blog.controller;

import com.raptiye.blog.dto.response.PostDetailResponse;
import com.raptiye.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final PostService postService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("posts", postService.getPublishedPostSnippets());
        model.addAttribute("title", "Raptiye Blog - Ana Sayfa");
        return "index";
    }

    @GetMapping("/post/{slug}")
    public String postDetail(@PathVariable String slug, Model model) {
        PostDetailResponse post = postService.getPostBySlug(slug, true);
        model.addAttribute("post", post);
        model.addAttribute("title", post.getTitle());
        return "post-detail";
    }

    @GetMapping("/design-test")
    public String designTest(Model model) {
        model.addAttribute("posts", postService.getPublishedPosts());
        model.addAttribute("title", "Tasarım test Sayfası");
        return "design-test";
    }
}
