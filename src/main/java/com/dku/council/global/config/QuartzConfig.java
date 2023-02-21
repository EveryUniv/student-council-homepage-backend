package com.dku.council.global.config;

import com.dku.council.domain.like.PostLikeDumpTask;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

@Configuration
public class QuartzConfig {
    private static final String ID_POST_LIKE_DUMP_TASK = "PostLikeDumpToDB";
    private static final String GROUP_MAIN = "main";


    @Bean
    public JobDetail postLikeDumpJob(){
        return JobBuilder.newJob(PostLikeDumpTask.class)
                .withIdentity(new JobKey(ID_POST_LIKE_DUMP_TASK, GROUP_MAIN))
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger postLikeDumpTrigger() {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInMinutes(30)
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(postLikeDumpJob())
                .withIdentity(new TriggerKey(ID_POST_LIKE_DUMP_TASK, GROUP_MAIN))
                .withSchedule(scheduleBuilder)
                .build();
    }
}
