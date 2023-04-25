package com.dku.council.domain.ticket.repository.impl;

import com.dku.council.domain.ticket.model.dto.TicketDto;
import com.dku.council.util.base.AbstractContainerRedisTest;
import com.dku.council.util.test.FullIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@FullIntegrationTest
class TicketRedisRepositoryTest extends AbstractContainerRedisTest {

    @Autowired
    private TicketRedisRepository repository;

    @Test
    @DisplayName("티켓팅 데이터 잘 입력되는가")
    void enroll() {
        // when
        int[] enrollResults = {
                repository.getMyTicket(1L, 1L),
                repository.enroll(1L, 1L),
                repository.enroll(1L, 1L),
                repository.enroll(2L, 1L),
                repository.enroll(3L, 1L),
                repository.enroll(3L, 1L),
                repository.getMyTicket(1L, 1L),
                repository.getMyTicket(2L, 1L),
                repository.getMyTicket(3L, 1L),
                repository.getMyTicket(4L, 1L)
        };
        int[] expected = {-1, 1, -1, 2, 3, -1, 1, 2, 3, -1};

        // then
        assertThat(enrollResults).containsExactly(expected);
    }

    @Test
    @DisplayName("티켓팅 데이터 강제 캐싱")
    void saveMyTicket() {
        // when
        int prevResult = repository.getMyTicket(1L, 1L);
        int saveResult = repository.saveMyTicket(1L, 1L, 5);
        int result = repository.getMyTicket(1L, 1L);

        // then
        assertThat(prevResult).isEqualTo(-1);
        assertThat(saveResult).isEqualTo(5);
        assertThat(result).isEqualTo(5);
    }

    @Test
    @DisplayName("티켓팅 데이터 모두 flush")
    void flushAllTickets() {
        // given
        List<TicketDto> expected = List.of(
                new TicketDto(1L, 1L, 1),
                new TicketDto(1L, 2L, 1),
                new TicketDto(2L, 1L, 2),
                new TicketDto(2L, 2L, 2),
                new TicketDto(3L, 1L, 3),
                new TicketDto(3L, 3L, 1)
        );
        for (TicketDto ticket : expected) {
            repository.enroll(ticket.getUserId(), ticket.getEventId());
        }
        repository.enroll(1L, 1L);
        repository.enroll(3L, 1L);

        // when
        List<TicketDto> tickets = repository.flushAllTickets();

        // then
        for (TicketDto ticket : tickets) {
            int getResult = repository.getMyTicket(ticket.getUserId(), ticket.getEventId());
            assertThat(getResult).isEqualTo(-1);
        }
        assertThat(tickets).containsExactlyInAnyOrderElementsOf(expected);
    }
}