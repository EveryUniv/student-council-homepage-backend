package com.dku.council.domain.batch;

import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.like.service.impl.CachedLikeServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeDumpScheduler {

    private final CachedLikeServiceImpl service;

    @Scheduled(fixedDelayString = "${app.post.like.dump-delay}")
    public void dumpToDB() {
        for (LikeTarget target : LikeTarget.values()) {
            if (service.dumpToDB(target) > 0) {
                log.info("{} likes in memory dump to DB.", target);
            }
        }
    }
}
