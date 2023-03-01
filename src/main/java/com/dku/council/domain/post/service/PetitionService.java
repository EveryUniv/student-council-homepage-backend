package com.dku.council.domain.post.service;

import com.dku.council.domain.category.repository.CategoryRepository;
import com.dku.council.domain.comment.model.dto.CommentDto;
import com.dku.council.domain.comment.service.CommentService;
import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.dto.response.ResponsePetitionDto;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.repository.PetitionRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.infra.nhn.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PetitionService extends GenericPostService<Petition> {

    private final CommentService commentService;
    private final int thresholdCommentCount;

    public PetitionService(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ViewCountService viewCountService,
                           FileUploadService fileUploadService,
                           MessageSource messageSource,
                           PetitionRepository repository,
                           CommentService CommentService,
                           @Value("${app.post.petition.threshold-comment-count}") int thresholdCommentCount) {
        super(repository, userRepository, categoryRepository, viewCountService, fileUploadService, messageSource);
        this.commentService = CommentService;
        this.thresholdCommentCount = thresholdCommentCount;
    }

    public ResponsePetitionDto findOnePetition(Long postId, Long userId, String remoteAddress) {
        Petition post = viewPost(postId, remoteAddress);
        return new ResponsePetitionDto(fileUploadService.getBaseURL(), userId, post);
    }

    public void reply(Long postId, String answer) {
        Petition post = findPost(postId);
        post.replyAnswer(answer);
        post.updatePetitionStatus(PetitionStatus.ANSWERED);
    }

    public Page<CommentDto> listComment(Long postId, Pageable pageable) {
        return commentService.list(postId, pageable);
    }

    public Long createComment(Long postId, Long userId, String text) {
        Petition post = findPost(postId);
        if (post.getPetitionStatus() == PetitionStatus.ACTIVE && post.getComments().size() >= thresholdCommentCount) { // todo 댓글 수 캐싱
            post.updatePetitionStatus(PetitionStatus.WAITING);
        }
        return commentService.create(postId, userId, text);
    }

    public Long deleteComment(Long id, Long userId) {
        return commentService.delete(id, userId, true);
    }
}
