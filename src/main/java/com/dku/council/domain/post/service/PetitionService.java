package com.dku.council.domain.post.service;

import com.dku.council.domain.comment.model.dto.CommentDto;
import com.dku.council.domain.comment.service.CommentService;
import com.dku.council.domain.post.exception.DuplicateAgreementException;
import com.dku.council.domain.post.exception.DuplicateCommentException;
import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.dto.list.SummarizedPetitionDto;
import com.dku.council.domain.post.model.dto.response.ResponsePetitionDto;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.statistic.model.dto.PetitionStatisticDto;
import com.dku.council.domain.statistic.service.PetitionStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PetitionService {

    private final GenericPostService<Petition> postService;
    private final CommentService commentService;
    private final PetitionStatisticService statisticService;

    @Value("${app.post.petition.threshold-comment-count}")
    private final int thresholdCommentCount;

    @Value("${app.post.petition.expires}")
    private final Duration expiresTime;


    @Transactional(readOnly = true)
    public Page<SummarizedPetitionDto> listPetition(Specification<Petition> spec, int bodySize, Pageable pageable) {
        return postService.list(spec, pageable, bodySize, (dto, post) ->
                new SummarizedPetitionDto(dto, post, expiresTime, statisticService.count(post.getId()))); // TODO 댓글 개수는 캐싱해서 사용하기 (반드시)
    }

    @Transactional
    public ResponsePetitionDto findOnePetition(Long postId, Long userId, String remoteAddress) {
        List<PetitionStatisticDto> top4Department = statisticService.findTop4Department(postId);
        int totalCount = statisticService.count(postId);
        return postService.findOne(postId, userId, remoteAddress, (dto, post) ->
                new ResponsePetitionDto(dto, post, expiresTime, totalCount, top4Department));
    }

    public void reply(Long postId, String answer) {
        Petition post = postService.findPost(postId);
        post.replyAnswer(answer);
        post.updatePetitionStatus(PetitionStatus.ANSWERED);
    }

    public void agreePetition(Long postId, Long userId) {
        Petition post = postService.findPost(postId);
        if(commentService.isCommentedAlready(postId, userId)) {
            throw new DuplicateAgreementException();
        }

        if(post.getExtraStatus() == PetitionStatus.ACTIVE && post.getComments().size() +1 >= thresholdCommentCount) { // todo 댓글 수 캐싱
            post.updatePetitionStatus(PetitionStatus.WAITING);
        }

        commentService.create(postId, userId, "동의합니다.");
        statisticService.save(postId, userId);
    }
}
