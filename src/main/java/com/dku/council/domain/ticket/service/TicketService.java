package com.dku.council.domain.ticket.service;

import com.dku.council.domain.ticket.exception.AlreadyIssuedTicketException;
import com.dku.council.domain.ticket.exception.NoTicketException;
import com.dku.council.domain.ticket.model.dto.TicketDto;
import com.dku.council.domain.ticket.model.dto.TicketEventDto;
import com.dku.council.domain.ticket.model.dto.request.RequestNewTicketEventDto;
import com.dku.council.domain.ticket.model.dto.response.ResponseTicketDto;
import com.dku.council.domain.ticket.model.entity.Ticket;
import com.dku.council.domain.ticket.model.entity.TicketEvent;
import com.dku.council.domain.ticket.repository.TicketEventRepository;
import com.dku.council.domain.ticket.repository.TicketMemoryRepository;
import com.dku.council.domain.ticket.repository.TicketRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketEventRepository ticketEventRepository;
    private final TicketRepository persistenceRepository;
    private final UserRepository userRepository;
    private final TicketMemoryRepository memoryRepository;

    @Transactional(readOnly = true)
    public List<TicketEventDto> list() {
        List<TicketEventDto> events = memoryRepository.findAllEvents();
        if (events != null) {
            return events;
        }
        List<TicketEvent> eventEntities = ticketEventRepository.findAll();
        return memoryRepository.saveEvents(eventEntities);
    }

    @Transactional
    public void newTicketEvent(RequestNewTicketEventDto dto) {
        ticketEventRepository.save(dto.createEntity());
    }

    @Transactional
    public void deleteTicketEvent(Long ticketEventId) {
        ticketEventRepository.deleteById(ticketEventId);
    }

    @Transactional(readOnly = true)
    public ResponseTicketDto myTicket(Long userId, Long ticketEventId) {
        int turn = memoryRepository.getMyTicket(userId, ticketEventId);
        if (turn == -1) {
            Ticket ticket = persistenceRepository.findByUserIdAndEventId(userId, ticketEventId)
                    .orElseThrow(NoTicketException::new);
            turn = memoryRepository.saveMyTicket(userId, ticketEventId, ticket.getTurn());
        }
        return new ResponseTicketDto(turn);
    }

    // TODO 시작 시간 확인
    public ResponseTicketDto enroll(Long userId, Long ticketEventId) {
        int turn = memoryRepository.enroll(userId, ticketEventId);
        if (turn == -1) {
            throw new AlreadyIssuedTicketException();
        }
        return new ResponseTicketDto(turn);
    }

    @Transactional
    public void dumpToDb() {
        List<TicketDto> tickets = memoryRepository.flushAllTickets();
        for (TicketDto dto : tickets) {
            User user = userRepository.getReferenceById(dto.getUserId());
            TicketEvent event = ticketEventRepository.getReferenceById(dto.getEventId());
            Ticket ticket = new Ticket(user, event, dto.getTurn());
            persistenceRepository.save(ticket);
        }
    }
}
