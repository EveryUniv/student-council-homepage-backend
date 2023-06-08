package com.dku.council.domain.post.service.post;

import com.dku.council.domain.post.exception.DuplicateAgreementException;
import com.dku.council.domain.post.exception.ExpiredPetitionException;
import com.dku.council.domain.post.exception.PostCooltimeException;
import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.dto.list.SummarizedPetitionDto;
import com.dku.council.domain.post.model.dto.request.RequestCreatePetitionDto;
import com.dku.council.domain.post.model.dto.response.ResponsePetitionDto;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.repository.PostTimeMemoryRepository;
import com.dku.council.domain.post.repository.post.PetitionRepository;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.statistic.model.dto.PetitionStatisticDto;
import com.dku.council.domain.statistic.service.PetitionStatisticService;
import com.dku.council.global.auth.role.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PetitionService {

    public static final String PETITION_KEY = "petition";

    private final GenericPostService<Petition> postService;
    private final PetitionStatisticService statisticService;
    private final PostTimeMemoryRepository postTimeMemoryRepository;
    private final PetitionRepository repository;

    private final Clock clock;

    @Value("${app.post.petition.threshold-comment-count}")
    private final int thresholdCommentCount;

    @Value("${app.post.petition.expires}")
    private final Duration expiresTime;

    @Value("${app.post.petition.write-cooltime}")
    private final Duration writeCooltime;


    @Transactional(readOnly = true)
    public Page<SummarizedPetitionDto> listPetition(String keyword, List<Long> tagIds, PetitionStatus status,
                                                    int bodySize, Pageable pageable) {
        Specification<Petition> spec = PostSpec.withTitleOrBody(keyword);
        spec = spec.and(PostSpec.withPetitionStatus(status));
        spec = spec.and(PostSpec.withTags(tagIds));
        return postService.list(repository, spec, pageable, bodySize, (dto, post) ->
                new SummarizedPetitionDto(dto, post, expiresTime, statisticService.count(post.getId())));
    }

    @Transactional
    public Long create(Long userId, RequestCreatePetitionDto dto) {
        Instant now = Instant.now(clock);
        if (postTimeMemoryRepository.isAlreadyContains(PETITION_KEY, userId, now)) {
            throw new PostCooltimeException("petition");
        }

        Long result = postService.create(repository, userId, dto);
        postTimeMemoryRepository.put(PETITION_KEY, userId, writeCooltime, now);
        return result;
    }

    @Transactional(readOnly = true)
    public ResponsePetitionDto findOnePetition(Long postId, Long userId, UserRole role, String remoteAddress) {
        List<PetitionStatisticDto> top4Department = statisticService.findTop4Department(postId);
        int totalCount = statisticService.count(postId);

        // 상위 4개 부서의 합이 전체 동의 수보다 작을 경우, 기타로 표시
        Optional<Long> reduce = top4Department.stream().map(PetitionStatisticDto::getAgreeCount).reduce(Long::sum);
        if (reduce.isPresent() && totalCount > reduce.get()) {
            top4Department.add(new PetitionStatisticDto("기타", totalCount - reduce.get()));
        }

        boolean agreed = statisticService.isAlreadyAgreed(postId, userId);
        return postService.findOne(repository, postId, userId, role, remoteAddress, (dto, post) ->
                new ResponsePetitionDto(dto, post, expiresTime, totalCount, top4Department, agreed));
    }

    @Transactional
    public void reply(Long postId, String answer) {
        Petition post = postService.findPost(repository, postId, UserRole.ADMIN);
        post.replyAnswer(answer);
        post.updatePetitionStatus(PetitionStatus.ANSWERED);
    }

    @Transactional
    public void agreePetition(Long postId, Long userId) {
        Petition post = postService.findPost(repository, postId, UserRole.USER);
        if (statisticService.isAlreadyAgreed(postId, userId)) {
            throw new DuplicateAgreementException();
        }

        if (post.getExtraStatus() == PetitionStatus.EXPIRED) {
            throw new ExpiredPetitionException();
        }

        int countAgree = statisticService.count(postId);
        if (post.getExtraStatus() == PetitionStatus.ACTIVE && countAgree + 1 >= thresholdCommentCount) {
            post.updatePetitionStatus(PetitionStatus.WAITING);
        }

        statisticService.save(postId, userId);
    }

    public void blind(Long id) {
        postService.blind(repository, id);
    }

    public void unblind(Long id) {
        postService.unblind(repository, id);
    }

    @Transactional(readOnly = true)
    public Page<SummarizedPetitionDto> listMyPosts(Long userId, Pageable pageable, int bodySize) {
        return repository.findAllByUserId(userId, pageable)
                .map(post -> {
                    SummarizedGenericPostDto dto = postService.makeListDto(bodySize, post);
                    return new SummarizedPetitionDto(dto, post, expiresTime, statisticService.count(post.getId()));
                });
    }
}
