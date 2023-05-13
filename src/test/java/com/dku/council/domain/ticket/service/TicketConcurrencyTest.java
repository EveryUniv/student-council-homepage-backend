package com.dku.council.domain.ticket.service;

import com.dku.council.domain.ticket.model.dto.response.ResponseTicketTurnDto;
import com.dku.council.domain.ticket.model.entity.TicketEvent;
import com.dku.council.domain.ticket.repository.TicketEventRepository;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.util.DateUtil;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.UserMock;
import com.dku.council.util.base.AbstractContainerRedisTest;
import com.dku.council.util.test.FullIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@FullIntegrationTest
public class TicketConcurrencyTest extends AbstractContainerRedisTest {

    private static final int USER_COUNT = 50;

    @Autowired
    private TicketService service;

    @Autowired
    private TicketEventRepository eventRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private UserRepository userRepository;

    private final List<User> users = new ArrayList<>();
    private final LocalDateTime now = LocalDateTime.of(2021, 1, 1, 0, 0);
    private TicketEvent event;

    @BeforeEach
    void setUp() {
        event = new TicketEvent("name", now.minusSeconds(1), now.plusSeconds(1), 1000);
        event = eventRepository.save(event);

        for (int i = 0; i < USER_COUNT; i++) {
            Major major = MajorMock.create();
            major = majorRepository.save(major);

            User user = UserMock.create(major);
            user = userRepository.save(user);

            users.add(user);
            users.add(user);
        }
    }

    @Test
    @DisplayName("티켓팅 동시에 k건 수행시, k건 성공")
    public void enroll() throws Exception {
        // given
        CountDownLatch latch = new CountDownLatch(users.size());
        Instant now = DateUtil.toInstant(this.now);
        ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();
        AtomicInteger failedCount = new AtomicInteger(0);

        // when
        for (User user : users) {
            new Thread(() -> {
                try {
                    ResponseTicketTurnDto dto = service.enroll(user.getId(), event.getId(), now);
                    map.put(dto.getTurn(), 0);
                } catch (Exception e) {
                    failedCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        latch.await();

        // then
        assertThat(map).hasSize(USER_COUNT);
        assertThat(failedCount.get()).isEqualTo(USER_COUNT);
        for (int i = 0; i < USER_COUNT; i++) {
            assertThat(map.containsKey(i)).isNotNull();
        }
    }
}
