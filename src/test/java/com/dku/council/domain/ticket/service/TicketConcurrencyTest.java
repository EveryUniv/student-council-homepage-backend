package com.dku.council.domain.ticket.service;

import com.dku.council.domain.ticket.model.dto.response.ResponseTicketDto;
import com.dku.council.domain.ticket.model.entity.TicketEvent;
import com.dku.council.domain.ticket.repository.TicketEventRepository;
import com.dku.council.global.util.DateUtil;
import com.dku.council.util.base.AbstractContainerRedisTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
// @FullIntegrationTest
public class TicketConcurrencyTest extends AbstractContainerRedisTest {

    private static final int THREAD_COUNT = 100;

    @Autowired
    private TicketService service;

    @Autowired
    private TicketEventRepository eventRepository;

    private final LocalDateTime now = LocalDateTime.of(2021, 1, 1, 0, 0);
    private TicketEvent event;

    @BeforeEach
    void setUp() {
        event = new TicketEvent("name", now.minusSeconds(1), now.plusSeconds(1), 1000);
        event = eventRepository.save(event);
    }

    @Test
    @DisplayName("티켓팅 동시에 k건 수행시, k건 성공")
    public void enroll() throws Exception {
        // given
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        Instant now = DateUtil.toInstant(this.now);
        ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();

        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int index = i;
            new Thread(() -> {
                ResponseTicketDto dto = service.enroll((long) index, event.getId(), now);
                map.put(dto.getTurn(), 0);
                latch.countDown();
            }).start();
        }
        latch.await();

        // then
        assertThat(map).hasSize(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {
            assertThat(map.containsKey(i)).isNotNull();
        }
    }
}
