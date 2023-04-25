package com.dku.council.domain.ticket.repository;

import com.dku.council.domain.ticket.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByUserIdAndEventId(Long userId, Long eventId);
}
