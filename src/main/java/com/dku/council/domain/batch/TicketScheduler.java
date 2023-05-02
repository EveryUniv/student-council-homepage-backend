package com.dku.council.domain.batch;

import com.dku.council.domain.ticket.model.dto.TicketDto;
import com.dku.council.domain.ticket.model.entity.Ticket;
import com.dku.council.domain.ticket.model.entity.TicketEvent;
import com.dku.council.domain.ticket.repository.TicketEventRepository;
import com.dku.council.domain.ticket.repository.TicketMemoryRepository;
import com.dku.council.domain.ticket.repository.TicketRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketScheduler {

    private final TicketRepository persistenceRepository;
    private final TicketMemoryRepository memoryRepository;
    private final TicketEventRepository ticketEventRepository;
    private final UserRepository userRepository;

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
