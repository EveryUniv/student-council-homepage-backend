package com.dku.council.domain.admin.service;

import com.dku.council.domain.comment.exception.CommentNotFoundException;
import com.dku.council.domain.comment.model.CommentStatus;
import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentPageService {
    private final CommentRepository commentRepository;

    public Comment findOne(Long id){
        return commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);
    }

    public void active(Long id){
        Comment comment = findOne(id);
        comment.updateStatus(CommentStatus.ACTIVE);
    }

    public void delete(Long id){
        Comment comment = findOne(id);
        comment.updateStatus(CommentStatus.DELETED_BY_ADMIN);
    }
}
