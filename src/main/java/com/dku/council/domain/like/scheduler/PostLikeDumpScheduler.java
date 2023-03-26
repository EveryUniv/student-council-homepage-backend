package com.dku.council.domain.like.scheduler;

import com.dku.council.domain.like.service.impl.RedisPostLikeServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostLikeDumpScheduler {

    private final RedisPostLikeServiceImpl service;

    @Scheduled(fixedDelayString = "${app.post.like-dump-delay}")
    public void dumpToDB() {
        if (service.dumpToDB() > 0) {
            log.info("PostLikes in memory dump to DB.");
        }
    }
}
