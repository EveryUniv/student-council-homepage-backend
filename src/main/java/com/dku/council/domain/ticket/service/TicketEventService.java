package com.dku.council.domain.ticket.service;

import com.dku.council.domain.ticket.exception.NoTicketEventException;
import com.dku.council.domain.ticket.model.dto.TicketEventDto;
import com.dku.council.domain.ticket.model.dto.request.RequestNewTicketEventDto;
import com.dku.council.domain.ticket.model.entity.TicketEvent;
import com.dku.council.domain.ticket.repository.TicketEventMemoryRepository;
import com.dku.council.domain.ticket.repository.TicketEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketEventService {

    private final TicketEventRepository persistenceRepository;
    private final TicketEventMemoryRepository memoryRepository;

    @Transactional(readOnly = true)
    public List<TicketEventDto> list() {
        List<TicketEventDto> events = memoryRepository.findAll();
        if (!events.isEmpty()) {
            return events;
        }

        List<TicketEvent> eventEntities = persistenceRepository.findAll();
        return memoryRepository.saveAll(eventEntities);
    }

    @Transactional
    public Long newTicketEvent(RequestNewTicketEventDto dto) {
        TicketEvent event = persistenceRepository.save(dto.createEntity());
        return memoryRepository.save(event).getId();
    }

    @Transactional
    public void deleteTicketEvent(Long ticketEventId) {
        persistenceRepository.deleteById(ticketEventId);
        memoryRepository.deleteById(ticketEventId);
    }

    @Transactional(readOnly = true)
    public TicketEventDto findEventById(Long ticketEventId) {
        return memoryRepository.findById(ticketEventId)
                .orElseGet(() -> {
                    TicketEvent entity = persistenceRepository.findById(ticketEventId)
                            .orElseThrow(NoTicketEventException::new);
                    return memoryRepository.save(entity);
                });
    }

}
