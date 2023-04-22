package com.dku.council.admin.page;

import com.dku.council.domain.tag.model.entity.Tag;
import com.dku.council.domain.tag.repository.TagRepository;
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
    private final TagRepository tagRepository;

    @GetMapping
    public String tags(Model model){
        List<Tag> all = tagRepository.findAll();
        model.addAttribute("tags", all);
        return "tag/tags";
    }

    @PostMapping("/{tagId}/delete")
    public String tagDelete(HttpServletRequest request, @PathVariable Long tagId){
        Tag tag = tagRepository.findById(tagId).get();
        tagRepository.delete(tag);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/{tagId}/rename")
    public String tagRename(HttpServletRequest request, @PathVariable Long tagId, String name){
        Tag tag = tagRepository.findById(tagId).get();
        tag.updateName(name);
        tagRepository.save(tag);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping
    public String createTag(HttpServletRequest request, String name){
        Tag tag = new Tag(name);
        tagRepository.save(tag);
        return "redirect:" + request.getHeader("Referer");
    }
}
