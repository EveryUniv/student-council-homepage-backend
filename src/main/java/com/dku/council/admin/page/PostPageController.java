package com.dku.council.admin.page;

import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.post.*;
import com.dku.council.domain.post.repository.spec.PostSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/manage/posts")
@RequiredArgsConstructor
public class PostPageController {
    private final int DEFAULT_PAGE_SIZE = 15;
    private final int DEFAULT_MAX_PAGE = 5;
    private final GenericPostRepository<Post> genericPostRepository;
    private final PostRepository postRepository;
    private final VocRepository vocRepository;
    private final PetitionRepository petitionRepository;
    private final GeneralForumRepository generalForumRepository;

    @GetMapping
    public String posts(Model model, @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String type,
                        @RequestParam(required = false) String status
    ){
        Specification<Post> spec = PostSpec.withTitleOrBody(keyword).and(PostSpec.withStatus(status));
        Page<Post> all;
        if (type == null) {
            all = genericPostRepository.findAll(spec, pageable);
        }else if(type.equals("GeneralForum")){
            all = generalForumRepository.findAll(spec, pageable);
        }else if(type.equals("Petition")){
            all = petitionRepository.findAll(spec, pageable);
        }else if(type.equals("Voc")){
            all = vocRepository.findAll(spec, pageable);
        }else{
            all = genericPostRepository.findAll(spec, pageable);
        }
        model.addAttribute("posts", all);
        model.addAttribute("maxPage", DEFAULT_MAX_PAGE);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        model.addAttribute("status", status);
        return "admin/posts";
    }

    @GetMapping("/{postId}")
    public String post(Model model, @PathVariable Long postId){
        Post post = postRepository.findByIdWithAdmin(postId).orElseThrow(PostNotFoundException::new);
        model.addAttribute("post", post);
        return "admin/post";
    }

    @PostMapping("/{postId}/delete")
    public String postDelete(HttpServletRequest request, @PathVariable Long postId){
        Post post = postRepository.findByIdWithAdmin(postId).get();
        post.markAsDeleted(true);
        postRepository.save(post);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/{postId}/blind")
    public String blind(HttpServletRequest request, @PathVariable Long postId){
        Post post = postRepository.findByIdWithAdmin(postId).get();
        post.blind();
        postRepository.save(post);
        return "redirect:" + request.getHeader("Referer");
    }
    @PostMapping("/{postId}/activate")
    public String activate(HttpServletRequest request, @PathVariable Long postId){
        Post post = postRepository.findByIdWithAdmin(postId).get();
        post.unblind();
        postRepository.save(post);
        return "redirect:" + request.getHeader("Referer");
    }

}
