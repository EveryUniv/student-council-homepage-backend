package com.dku.council.domain.post.service;

import com.dku.council.domain.post.model.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    public void increasePostViews(Post post, String remoteAddress){
        // TODO Implementation
    }
}
