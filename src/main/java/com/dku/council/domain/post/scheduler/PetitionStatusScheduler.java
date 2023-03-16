package com.dku.council.domain.post.scheduler;

import com.dku.council.domain.post.repository.PetitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PetitionStatusScheduler {

    private final PetitionRepository petitionRepository;

    @Value("${app.post.petition.expires}")
    private final Duration petitionExpires;


    @Scheduled(cron = "0 0 0 * * *")
    public void schedule() {
        LocalDateTime time = LocalDateTime.now().minus(petitionExpires);
        petitionRepository.updateExpiredPetition(time);
    }
}
