package com.dku.council.domain.ticket.service;

import com.dku.council.domain.ticket.model.dto.TicketEventDto;
import com.dku.council.domain.ticket.model.dto.request.RequestNewTicketEventDto;
import com.dku.council.domain.ticket.model.entity.TicketEvent;
import com.dku.council.domain.ticket.repository.TicketEventMemoryRepository;
import com.dku.council.domain.ticket.repository.TicketEventRepository;
import com.dku.council.mock.TicketEventMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketEventServiceTest {

    @Mock
    private TicketEventRepository persistenceRepository;

    @Mock
    private TicketEventMemoryRepository memoryRepository;

    @InjectMocks
    private TicketEventService service;

    private final TicketEvent testEntity = TicketEventMock.createDummy();

    private final TicketEventDto testDto = new TicketEventDto(testEntity);


    @Test
    @DisplayName("티켓 이벤트 목록 조회 - 캐싱된 경우")
    void list() {
        // given
        when(memoryRepository.findAll()).thenReturn(List.of(testDto));

        // when
        List<TicketEventDto> result = service.list();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testDto);
    }

    @Test
    @DisplayName("티켓 이벤트 목록 조회 - 캐싱안된 경우")
    void listNoCached() {
        // given
        when(memoryRepository.findAll()).thenReturn(List.of());
        when(memoryRepository.saveAll(any())).thenReturn(List.of(testDto));
        when(persistenceRepository.findAll()).thenReturn(List.of(testEntity));

        // when
        List<TicketEventDto> result = service.list();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testDto);
    }

    @Test
    @DisplayName("티켓 이벤트 생성")
    void newTicketEvent() {
        // given
        RequestNewTicketEventDto dto = new RequestNewTicketEventDto("티켓 이벤트",
                LocalDateTime.of(2021, 1, 1, 0, 0),
                LocalDateTime.of(2021, 1, 2, 0, 0),
                1000);

        when(persistenceRepository.save(any(TicketEvent.class)))
                .thenReturn(testEntity);

        // when
        service.newTicketEvent(dto);

        // then
        verify(persistenceRepository, times(1))
                .save(argThat(argument -> isSameTicketRequest(dto, argument)));
        verify(memoryRepository, times(1))
                .save(argThat(argument -> isSameTicketRequest(dto, argument)));
    }

    private boolean isSameTicketRequest(RequestNewTicketEventDto dto, TicketEvent ticketEvent) {
        assertThat(dto.getName()).isEqualTo(ticketEvent.getName());
        assertThat(dto.getStartAt()).isEqualTo(ticketEvent.getBegin());
        assertThat(dto.getEndAt()).isEqualTo(ticketEvent.getEnd());
        assertThat(dto.getTotalTickets()).isEqualTo(ticketEvent.getTotalTickets());
        return true;
    }

    @Test
    @DisplayName("티켓 이벤트 삭제")
    void deleteTicketEvent() {
        // given
        Long ticketEventId = 1L;

        // when
        service.deleteTicketEvent(ticketEventId);

        // then
        verify(persistenceRepository, times(1)).deleteById(ticketEventId);
        verify(memoryRepository, times(1)).deleteById(ticketEventId);
    }

    @Test
    @DisplayName("ID로 티켓 이벤트 조회 - 캐싱된 경우")
    void findEventById() {
        // given
        when(memoryRepository.findById(1L)).thenReturn(Optional.of(testDto));

        // when
        TicketEventDto result = service.findEventById(1L);

        // then
        assertThat(result).isEqualTo(testDto);
    }

    @Test
    @DisplayName("ID로 티켓 이벤트 조회 - 캐싱안된 경우")
    void findEventByIdNoCached() {
        // given
        when(memoryRepository.findById(1L)).thenReturn(Optional.empty());
        when(memoryRepository.save(any())).thenReturn(testDto);
        when(persistenceRepository.findById(1L)).thenReturn(Optional.of(testEntity));

        // when
        TicketEventDto result = service.findEventById(1L);

        // then
        assertThat(result).isEqualTo(testDto);
    }
}