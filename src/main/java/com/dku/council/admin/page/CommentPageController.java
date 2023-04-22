package com.dku.council.admin.page;

import com.dku.council.domain.comment.model.CommentStatus;
import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/manage/comment")
@RequiredArgsConstructor
public class CommentPageController {
    private final CommentRepository commentRepository;
    @PostMapping("/{commentId}/delete")
    public String deActivate(HttpServletRequest request, @PathVariable Long commentId){
        Comment comment = commentRepository.findById(commentId).get();
        comment.updateStatus(CommentStatus.DELETED_BY_ADMIN);
        commentRepository.save(comment);
        return "redirect:" + request.getHeader("Referer");
    }
    @PostMapping("/{commentId}/activate")
    public String activeComment(HttpServletRequest request, @PathVariable Long commentId){
        Comment comment = commentRepository.findById(commentId).get();
        comment.updateStatus(CommentStatus.ACTIVE);
        commentRepository.save(comment);
        return "redirect:" + request.getHeader("Referer");
    }

}
