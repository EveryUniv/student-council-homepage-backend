package com.dku.council.domain.post.service;

import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.ViewCountMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class ViewCountService {

    private final ViewCountMemoryRepository memoryRepository;
    private final Clock clock;

    @Value("${app.post.view-counting-expires}")
    private final Duration expiresAfter;

    /**
     * 조회수 증가 처리. 동일성은 (remoteAddress, postId)로 구분하며, n분에 1회씩만 증가시킬 수 있다.
     * n분은 post.view-counting-duration 설정을 통해 분 단위로 조절할 수 있다.
     *
     * @param post          증가 대상 post
     * @param remoteAddress client의 remote address
     */
    public void increasePostViews(Post post, String remoteAddress) {
        Instant now = Instant.now(clock);
        if (!memoryRepository.isAlreadyContains(post.getId(), remoteAddress, now)) {
            memoryRepository.put(post.getId(), remoteAddress, expiresAfter, now);
            post.increaseViewCount();
        }
    }
}
