package com.dku.council.admin.page;

import com.dku.council.domain.comment.CommentRepository;
import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.post.PostRepository;
import com.dku.council.domain.user.model.UserStatus;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/manage/users")
@RequiredArgsConstructor
public class UserPageController {
    private final int DEFAULT_PAGE_SIZE = 15;
    private final int DEFAULT_MAX_PAGE = 5;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @GetMapping
    public String users(Model model, @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable, @Nullable String nickname){
        Page<User> all;
        if(nickname == null){
            all = userRepository.findAll(pageable);
        }else{
            all = userRepository.findAllByNicknameContaining(nickname, pageable);
        }
        model.addAttribute("users", all);
        model.addAttribute("maxPage", DEFAULT_MAX_PAGE);
        model.addAttribute("nickname", nickname);
        return "user/users";
    }

    @PostMapping("/{userId}/activate")
    public String active(HttpServletRequest request, @PathVariable Long userId){
        User user = userRepository.findById(userId).get();
        user.changeStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/{userId}/deactivate")
    public String deActive(HttpServletRequest request, @PathVariable Long userId){
        User user = userRepository.findById(userId).orElseThrow();
        user.changeStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/{userId}/comments")
    public String comments(Model model, @PathVariable Long userId, @PageableDefault(size = DEFAULT_PAGE_SIZE,
            sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<Comment> all = commentRepository.findAllByUserIdWithAdmin(userId, pageable);
        User user = userRepository.findById(userId).get();
        model.addAttribute("comments", all);
        model.addAttribute("maxPage", DEFAULT_MAX_PAGE);
        model.addAttribute("user", user);
        return "user/comments";
    }

    @GetMapping("/{userId}/posts")
    public String posts(Model model, @PathVariable Long userId, @PageableDefault(size = DEFAULT_PAGE_SIZE,
            sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        Page<Post> all = postRepository.findAllByUserIdWithAdmin(userId, pageable);
        User user = userRepository.findById(userId).get();
        model.addAttribute("posts", all);
        model.addAttribute("maxPage", DEFAULT_MAX_PAGE);
        model.addAttribute("user", user);
        return "user/posts";
    }
}
