package com.dku.council.domain.admin.controller;

import com.dku.council.domain.admin.dto.TagPageDto;
import com.dku.council.domain.admin.service.TagPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/manage/tags")
@RequiredArgsConstructor
public class TagPageController {
    private final TagPageService service;

    @GetMapping
    public String tags(Model model) {
        List<TagPageDto> all = service.list();
        model.addAttribute("tags", all);
        return "page/tag/tags";
    }

    @PostMapping("/{tagId}/delete")
    public String tagDelete(HttpServletRequest request, @PathVariable Long tagId) {
        service.delete(tagId);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/{tagId}/rename")
    public String tagRename(HttpServletRequest request, @PathVariable Long tagId, String name) {
        service.rename(tagId, name);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping
    public String createTag(HttpServletRequest request, String name) {
        service.create(name);
        return "redirect:" + request.getHeader("Referer");
    }
}
