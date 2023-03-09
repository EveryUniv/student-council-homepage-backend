package com.dku.council.domain.post.service;

import com.dku.council.domain.post.model.VocStatus;
import com.dku.council.domain.post.model.dto.response.ResponseVocDto;
import com.dku.council.domain.post.model.entity.posttype.Voc;
import com.dku.council.infra.nhn.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VocService {

    private final GenericPostService<Voc> postService;
    private final FileUploadService fileUploadService;


    @Transactional(readOnly = true)
    public ResponseVocDto findOne(Long postId, Long userId, String remoteAddress) {
        Voc post = postService.viewPost(postId, remoteAddress);
        return new ResponseVocDto(fileUploadService.getBaseURL(), userId, post);
    }

    public void reply(Long postId, String answer) {
        Voc post = postService.findPost(postId);
        post.replyAnswer(answer);
        post.updateVocStatus(VocStatus.ANSWERED);
    }
}
