package com.dku.council.domain.homebus.service;

import com.dku.council.domain.homebus.model.entity.HomeBus;
import com.dku.council.domain.homebus.repository.HomeBusRepository;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.HomeBusMock;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.UserMock;
import com.dku.council.util.base.AbstractContainerRedisTest;
import com.dku.council.util.test.FullIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@FullIntegrationTest
class HomeBusUserServiceConcurrencyTest extends AbstractContainerRedisTest {

    private static final int USER_COUNT = 50;
    private static final int SEATS = 30;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private HomeBusRepository busRepository;

    @Autowired
    private HomeBusUserService service;


    private HomeBus bus;
    private final List<User> users = new ArrayList<>();


    @BeforeEach
    void setUp() {
        bus = HomeBusMock.createWithSeats(SEATS);
        bus = busRepository.save(bus);

        for (int i = 0; i < USER_COUNT; i++) {
            Major major = MajorMock.create();
            major = majorRepository.save(major);

            User user = UserMock.create(major);
            user = userRepository.save(user);

            users.add(user);
        }
    }

    @Test
    @DisplayName("동시에 여러 명이 신청해도 일관성을 유지하는가?")
    void createTicketConcurrency() throws InterruptedException {
        // given
        ExecutorService pool = Executors.newFixedThreadPool(users.size());
        CountDownLatch latch = new CountDownLatch(users.size());
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        // when
        for (User user : users) {
            pool.execute(() -> {
                try {
                    service.createTicket(user.getId(), bus.getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                    failedCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        assertThat(successCount.get()).isEqualTo(SEATS);
        assertThat(failedCount.get()).isEqualTo(USER_COUNT - SEATS);
    }
}