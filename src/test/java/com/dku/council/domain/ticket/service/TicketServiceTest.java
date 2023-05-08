package com.dku.council.domain.ticket.service;

import com.dku.council.domain.ticket.exception.BeforeTicketPeriodException;
import com.dku.council.domain.ticket.model.dto.TicketEventDto;
import com.dku.council.domain.ticket.model.dto.response.ResponseTicketTurnDto;
import com.dku.council.domain.ticket.model.entity.Ticket;
import com.dku.council.domain.ticket.model.entity.TicketEvent;
import com.dku.council.domain.ticket.repository.TicketMemoryRepository;
import com.dku.council.domain.ticket.repository.TicketRepository;
import com.dku.council.domain.user.exception.NotAttendingException;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.service.UserInfoService;
import com.dku.council.global.util.DateUtil;
import com.dku.council.mock.TicketEventMock;
import com.dku.council.mock.UserInfoMock;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    private UserInfoService infoCacheService;

    @InjectMocks
    private TicketService service;


    @Test
    @DisplayName("내 티켓 조회 - 캐싱된 경우")
    void myTicket() {
        // given
        when(memoryRepository.getMyTicket(1L, 1L)).thenReturn(3);

        // when
        ResponseTicketTurnDto dto = service.myReservationOrder(1L, 1L);

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
        ResponseTicketTurnDto dto = service.myReservationOrder(1L, 1L);

        // then
        assertThat(dto.getTurn()).isEqualTo(3);
    }

    @Test
    @DisplayName("티켓 발급 실패 - 재학중이 아닌 경우")
    void failedEnrollByNotAttending() {
        // given
        LocalDateTime now = LocalDateTime.now(clock);
        TicketEventDto event = new TicketEventDto(1L, "test",
                now.minusSeconds(1), now.plusSeconds(1));

        when(ticketEventService.findEventById(1L)).thenReturn(event);
        when(infoCacheService.getUserInfo(eq(1L)))
                .thenReturn(UserInfoMock.create("졸업"));

        // when & then
        Assertions.assertThrows(NotAttendingException.class,
                () -> service.enroll(1L, 1L, DateUtil.toInstant(now)));
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
        Assertions.assertThrows(BeforeTicketPeriodException.class,
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
        when(infoCacheService.getUserInfo(eq(1L)))
                .thenReturn(UserInfoMock.create());
        when(memoryRepository.enroll(eq(1L), eq(1L), any())).thenReturn(5);

        // when
        ResponseTicketTurnDto result = service.enroll(1L, 1L, DateUtil.toInstant(now));

        // then
        assertThat(result.getTurn()).isEqualTo(5);
    }
}