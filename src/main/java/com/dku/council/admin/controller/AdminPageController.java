package com.dku.council.admin.controller;

import com.dku.council.domain.comment.CommentRepository;
import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.post.PostRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/manage")
@RequiredArgsConstructor
public class AdminPageController {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @GetMapping("/users")
    public String users(Model model){
        List<User> all = userRepository.findAll();
        model.addAttribute("users", all);
        return "admin/users";
    }

    @GetMapping("/posts/{userId}")
    public String posts(Model model, @PathVariable Long userId){
        List<Post> all = postRepository.findAllByUserId(userId);
        model.addAttribute("posts", all);
        return "admin/posts";
    }

    @GetMapping("/posts")
    public String posts(Model model){
        List<Post> all = postRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("posts", all);
        return "admin/posts";
    }

    @GetMapping("/comments/{userId}")
    public String comments(Model model, @PathVariable Long userId){
        List<Comment> all = commentRepository.findAllByUserId(userId);
        model.addAttribute("comments", all);
        return "admin/comments";
    }

    @GetMapping("/post/{postId}")
    public String post(Model model, @PathVariable Long postId){
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        model.addAttribute("post", post);
        return "admin/post";
    }
}
