package com.dku.council.domain.post.service;

import com.dku.council.domain.post.model.VocStatus;
import com.dku.council.domain.post.model.dto.response.ResponseVocDto;
import com.dku.council.domain.post.model.entity.posttype.Voc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VocService {

    private final GenericPostService<Voc> postService;


    @Transactional
    public ResponseVocDto findOne(Long postId, Long userId, String remoteAddress) {
        return postService.findOne(postId, userId, remoteAddress, ResponseVocDto::new);
    }

    public void reply(Long postId, String answer, Long userId) {
        Voc post = postService.findPost(postId, userId);
        post.replyAnswer(answer);
        post.updateVocStatus(VocStatus.ANSWERED);
    }
}
