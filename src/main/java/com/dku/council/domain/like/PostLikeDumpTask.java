package com.dku.council.domain.like;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class PostLikeDumpTask extends QuartzJobBean {

    @Autowired
    PostLikeService service;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        if (service.dumpToDB() > 0) {
            log.info("PostLikes in memory dump to DB.");
        }
    }
}
