package com.dku.council.domain.post.service;

import com.dku.council.domain.post.exception.PostCooltimeException;
import com.dku.council.domain.post.model.dto.request.RequestCreateGeneralForumDto;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.post.repository.PostTimeMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class GeneralForumService {

    public static final String GENERAL_FORUM_KEY = "generalForum";

    private final GenericPostService<GeneralForum> postService;
    private final PostTimeMemoryRepository postTimeMemoryRepository;

    private final Clock clock;

    @Value("${app.post.general-forum.write-cooltime}")
    private final Duration writeCooltime;

    @Transactional
    public Long create(Long userId, RequestCreateGeneralForumDto dto) {
        Instant now = Instant.now(clock);
        if (postTimeMemoryRepository.isAlreadyContains(GENERAL_FORUM_KEY, userId, now)) {
            throw new PostCooltimeException("general-forum");
        }

        Long result = postService.create(userId, dto);
        postTimeMemoryRepository.put(GENERAL_FORUM_KEY, userId, writeCooltime, now);
        return result;
    }
}
