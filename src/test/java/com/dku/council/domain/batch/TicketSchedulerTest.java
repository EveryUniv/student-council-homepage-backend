package com.dku.council.domain.batch;

import com.dku.council.domain.ticket.model.dto.TicketDto;
import com.dku.council.domain.ticket.model.entity.TicketEvent;
import com.dku.council.domain.ticket.repository.TicketEventRepository;
import com.dku.council.domain.ticket.repository.TicketMemoryRepository;
import com.dku.council.domain.ticket.repository.TicketRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.TicketEventMock;
import com.dku.council.mock.UserMock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketSchedulerTest {

    @Mock
    private TicketRepository persistenceRepository;

    @Mock
    private TicketMemoryRepository memoryRepository;

    @Mock
    private TicketEventRepository ticketEventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketScheduler ticketScheduler;

    @Test
    @DisplayName("메모리에 저장된 티켓을 DB에 저장")
    void dumpToDb() {
        // given
        List<TicketDto> tickets = List.of(
                new TicketDto(1L, 1L, 1),
                new TicketDto(2L, 2L, 1),
                new TicketDto(3L, 3L, 1));
        User user = UserMock.createDummyMajor();
        TicketEvent ticketEvent = TicketEventMock.createDummy();

        when(memoryRepository.flushAllTickets()).thenReturn(tickets);
        when(userRepository.getReferenceById(any())).thenReturn(user);
        when(ticketEventRepository.getReferenceById(any())).thenReturn(ticketEvent);

        // when
        ticketScheduler.dumpToDb();

        // then
        verify(persistenceRepository, Mockito.times(3)).save(argThat(ticket -> {
            Assertions.assertThat(ticket.getUser().getId()).isEqualTo(user.getId());
            Assertions.assertThat(ticket.getEvent().getId()).isEqualTo(ticketEvent.getId());
            Assertions.assertThat(ticket.getTurn()).isEqualTo(1);
            return true;
        }));
    }
}