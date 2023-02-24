package com.dku.council.domain.comment.service.impl;

import com.dku.council.domain.comment.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Override
    public void add(Long postId, Long userId, String content) {
        // TODO Implementation
    }

    @Override
    public void edit(Long commentId, Long userId, String content) {
        // TODO Implementation
    }

    @Override
    public void delete(Long commentId, Long userId, boolean isAdmin) {
        // TODO Implementation
    }
}
