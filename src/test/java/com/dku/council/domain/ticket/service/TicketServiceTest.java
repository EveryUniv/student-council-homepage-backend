package com.dku.council.domain.ticket.service;

import com.dku.council.domain.ticket.exception.AlreadyIssuedTicketException;
import com.dku.council.domain.ticket.exception.InvalidTicketPeriodException;
import com.dku.council.domain.ticket.model.dto.TicketEventDto;
import com.dku.council.domain.ticket.model.dto.response.ResponseTicketDto;
import com.dku.council.domain.ticket.model.entity.Ticket;
import com.dku.council.domain.ticket.model.entity.TicketEvent;
import com.dku.council.domain.ticket.repository.TicketMemoryRepository;
import com.dku.council.domain.ticket.repository.TicketRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.util.DateUtil;
import com.dku.council.mock.TicketEventMock;
import com.dku.council.mock.UserMock;
import com.dku.council.util.ClockUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    private static final Clock clock = ClockUtil.create();

    @Mock
    private TicketRepository persistenceRepository;

    @Mock
    private TicketMemoryRepository memoryRepository;

    @Mock
    private TicketEventService ticketEventService;

    @InjectMocks
    private TicketService service;


    @Test
    @DisplayName("내 티켓 조회 - 캐싱된 경우")
    void myTicket() {
        // given
        when(memoryRepository.getMyTicket(1L, 1L)).thenReturn(3);

        // when
        ResponseTicketDto dto = service.myTicket(1L, 1L);

        // then
        assertThat(dto.getTurn()).isEqualTo(3);
    }

    @Test
    @DisplayName("내 티켓 조회 - 캐싱안된 경우")
    void myTicketNoCached() {
        // given
        User user = UserMock.createDummyMajor();
        TicketEvent event = TicketEventMock.createDummy();
        Ticket ticket = new Ticket(user, event, 3);

        when(memoryRepository.getMyTicket(1L, 1L)).thenReturn(-1);
        when(persistenceRepository.findByUserIdAndEventId(1L, 1L)).thenReturn(Optional.of(ticket));
        when(memoryRepository.saveMyTicket(1L, 1L, ticket.getTurn())).thenReturn(3);

        // when
        ResponseTicketDto dto = service.myTicket(1L, 1L);

        // then
        assertThat(dto.getTurn()).isEqualTo(3);
    }

    @Test
    @DisplayName("티켓 발급 실패 - 이벤트 기간이 아닌 경우")
    void failedEnrollByNotPeriod() {
        // given
        LocalDateTime now = LocalDateTime.now(clock);
        TicketEventDto event = new TicketEventDto(1L, "test",
                now.plusSeconds(1), now.plusSeconds(2));

        when(ticketEventService.findEventById(1L)).thenReturn(event);

        // when & then
        Assertions.assertThrows(InvalidTicketPeriodException.class,
                () -> service.enroll(1L, 1L, DateUtil.toInstant(now)));
    }

    @Test
    @DisplayName("티켓 발급 실패 - 이미 티켓이 존재하는 경우")
    void failedEnrollByAlready() {
        // given
        LocalDateTime now = LocalDateTime.now(clock);
        TicketEventDto event = new TicketEventDto(1L, "test",
                now.minusSeconds(1), now.plusSeconds(1));

        when(ticketEventService.findEventById(1L)).thenReturn(event);
        when(memoryRepository.enroll(1L, 1L)).thenReturn(-1);

        // when & then
        Assertions.assertThrows(AlreadyIssuedTicketException.class,
                () -> service.enroll(1L, 1L, DateUtil.toInstant(now)));
    }

    @Test
    @DisplayName("티켓 발급")
    void enroll() {
        // given
        LocalDateTime now = LocalDateTime.now(clock);
        TicketEventDto event = new TicketEventDto(1L, "test",
                now.minusNanos(1), now.plusNanos(1));

        when(ticketEventService.findEventById(1L)).thenReturn(event);
        when(memoryRepository.enroll(1L, 1L)).thenReturn(5);

        // when
        ResponseTicketDto result = service.enroll(1L, 1L, DateUtil.toInstant(now));

        // then
        assertThat(result.getTurn()).isEqualTo(5);
    }
}