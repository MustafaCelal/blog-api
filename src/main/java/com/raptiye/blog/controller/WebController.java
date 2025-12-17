package com.raptiye.blog.controller;

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
        model.addAttribute("posts", postService.getPublishedPosts());
        return "index";
    }

    @GetMapping("/post/{slug}")
    public String postDetail(@PathVariable String slug, Model model) {
        model.addAttribute("post", postService.getPostBySlug(slug, true));
        return "post-detail";
    }

    @GetMapping("/design-test")
    public String designTest() {
        return "bootstrap_blog";
    }
}
