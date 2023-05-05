package com.dku.council.domain.post.service.post;

import com.dku.council.domain.post.exception.PostCooltimeException;
import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateGeneralForumDto;
import com.dku.council.domain.post.model.dto.response.ResponseGeneralForumDto;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.post.repository.PostTimeMemoryRepository;
import com.dku.council.domain.post.repository.post.GeneralForumRepository;
import com.dku.council.domain.post.repository.spec.PostSpec;
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

@Service
@RequiredArgsConstructor
public class GeneralForumService {

    public static final String GENERAL_FORUM_KEY = "generalForum";

    private final GenericPostService<GeneralForum> postService;
    private final PostTimeMemoryRepository postTimeMemoryRepository;
    private final GeneralForumRepository repository;

    private final Clock clock;

    @Value("${app.post.general-forum.write-cooltime}")
    private final Duration writeCooltime;

    public Long create(Long userId, RequestCreateGeneralForumDto dto) {
        Instant now = Instant.now(clock);
        if (postTimeMemoryRepository.isAlreadyContains(GENERAL_FORUM_KEY, userId, now)) {
            throw new PostCooltimeException("general-forum");
        }

        Long result = postService.create(repository, userId, dto);
        postTimeMemoryRepository.put(GENERAL_FORUM_KEY, userId, writeCooltime, now);
        return result;
    }

    public Page<SummarizedGenericPostDto> list(String keyword, List<Long> tagIds, Pageable pageable,
                                               int bodySize) {
        Specification<GeneralForum> spec = PostSpec.withTags(tagIds);
        spec = spec.and(PostSpec.withTitleOrBody(keyword));
        return postService.list(repository, spec, pageable, bodySize);
    }

    public ResponseGeneralForumDto findOne(Long id, Long userId, UserRole role, String address) {
        return postService.findOne(repository, id, userId, role, address, ResponseGeneralForumDto::new);
    }

    public void delete(Long id, Long userId, boolean admin) {
        postService.delete(repository, id, userId, admin);
    }

    public void blind(Long id) {
        postService.blind(repository, id);
    }

    public void unblind(Long id) {
        postService.unblind(repository, id);
    }

    @Transactional(readOnly = true)
    public Page<SummarizedGenericPostDto> listMyPosts(Long userId, Pageable pageable, int bodySize) {
        return repository.findAllByUserId(userId, pageable)
                .map(post -> postService.makeListDto(bodySize, post));
    }
}
