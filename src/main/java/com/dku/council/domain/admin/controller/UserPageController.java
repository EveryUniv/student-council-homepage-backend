package com.dku.council.domain.admin.controller;

import com.dku.council.domain.admin.dto.CommentPageDto;
import com.dku.council.domain.admin.dto.PostPageDto;
import com.dku.council.domain.admin.dto.UserPageDto;
import com.dku.council.domain.admin.service.UserPageService;
import com.dku.council.domain.user.model.entity.User;
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

import static com.dku.council.domain.admin.util.PageConstants.DEFAULT_MAX_PAGE;
import static com.dku.council.domain.admin.util.PageConstants.DEFAULT_PAGE_SIZE;

@Controller
@RequestMapping("/manage/users")
@RequiredArgsConstructor
public class UserPageController {
    private final UserPageService service;

    @GetMapping
    public String users(Model model, @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable, @Nullable String nickname) {
        Page<UserPageDto> all = service.list(nickname, pageable);
        model.addAttribute("users", all);
        model.addAttribute("maxPage", DEFAULT_MAX_PAGE);
        model.addAttribute("nickname", nickname);
        return "page/user/users";
    }

    @PostMapping("/{userId}/activate")
    public String active(HttpServletRequest request, @PathVariable Long userId) {
        service.activeUser(userId);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/{userId}/deactivate")
    public String deActive(HttpServletRequest request, @PathVariable Long userId) {
        service.deActiveUser(userId);
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/{userId}/comments")
    public String comments(Model model, @PathVariable Long userId, @PageableDefault(size = DEFAULT_PAGE_SIZE,
            sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommentPageDto> all = service.commentList(userId, pageable);
        User user = service.findUser(userId);
        model.addAttribute("comments", all);
        model.addAttribute("maxPage", DEFAULT_MAX_PAGE);
        model.addAttribute("user", user);
        return "page/user/comments";
    }

    @GetMapping("/{userId}/posts")
    public String posts(Model model, @PathVariable Long userId, @PageableDefault(size = DEFAULT_PAGE_SIZE,
            sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostPageDto> all = service.postList(userId, pageable);
        User user = service.findUser(userId);
        model.addAttribute("posts", all);
        model.addAttribute("maxPage", DEFAULT_MAX_PAGE);
        model.addAttribute("user", user);
        return "page/user/posts";
    }
}
