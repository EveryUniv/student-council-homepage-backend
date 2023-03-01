package com.dku.council.domain.post.service;

import com.dku.council.domain.comment.model.dto.CommentDto;
import com.dku.council.domain.comment.service.CommentService;
import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.dto.response.ResponsePetitionDto;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.infra.nhn.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PetitionService {

    private final GenericPostService<Petition> postService;
    private final CommentService commentService;
    private final FileUploadService fileUploadService;

    @Value("${app.post.petition.threshold-comment-count}")
    private final int thresholdCommentCount;


    @Transactional(readOnly = true)
    public ResponsePetitionDto findOnePetition(Long postId, Long userId, String remoteAddress) {
        Petition post = postService.viewPost(postId, remoteAddress);
        return new ResponsePetitionDto(fileUploadService.getBaseURL(), userId, post);
    }

    public void reply(Long postId, String answer) {
        Petition post = postService.findPost(postId);
        post.replyAnswer(answer);
        post.updatePetitionStatus(PetitionStatus.ANSWERED);
    }

    @Transactional(readOnly = true)
    public Page<CommentDto> listComment(Long postId, Pageable pageable) {
        return commentService.list(postId, pageable);
    }

    public Long createComment(Long postId, Long userId, String text) {
        Petition post = postService.findPost(postId);
        if (post.getPetitionStatus() == PetitionStatus.ACTIVE && post.getComments().size() >= thresholdCommentCount) { // todo 댓글 수 캐싱
            post.updatePetitionStatus(PetitionStatus.WAITING);
        }
        return commentService.create(postId, userId, text);
    }

    public Long deleteComment(Long id, Long userId) {
        return commentService.delete(id, userId, true);
    }
}
