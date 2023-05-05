package com.dku.council.domain.admin.controller;

import com.dku.council.domain.admin.dto.PostPageDto;
import com.dku.council.domain.admin.service.PostPageService;
import com.dku.council.domain.admin.util.PageConstants;
import com.dku.council.domain.post.model.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.dku.council.domain.admin.util.PageConstants.DEFAULT_MAX_PAGE;

@Controller
@RequestMapping("/manage/posts")
@RequiredArgsConstructor
public class PostPageController {
    private final PostPageService service;

    @GetMapping
    public String posts(Model model,
                        @PageableDefault(size = PageConstants.DEFAULT_PAGE_SIZE,
                                sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String type,
                        @RequestParam(required = false) String status
    ) {
        Page<PostPageDto> all = service.list(keyword, type, status, pageable);
        model.addAttribute("posts", all);
        model.addAttribute("maxPage", DEFAULT_MAX_PAGE);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        model.addAttribute("status", status);
        return "page/post/posts";
    }

    @GetMapping("/{postId}")
    public String post(Model model, @PathVariable Long postId) {
        Post post = service.findOne(postId);
        model.addAttribute("post", post);
        return "page/post/post";
    }

    @PostMapping("/{postId}/delete")
    public String postDelete(HttpServletRequest request, @PathVariable Long postId) {
        service.delete(postId);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/{postId}/blind")
    public String blind(HttpServletRequest request, @PathVariable Long postId) {
        service.blind(postId);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/{postId}/activate")
    public String activate(HttpServletRequest request, @PathVariable Long postId) {
        service.active(postId);
        return "redirect:" + request.getHeader("Referer");
    }

}
