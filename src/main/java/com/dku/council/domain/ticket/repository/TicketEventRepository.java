package com.dku.council.domain.ticket.repository;

import com.dku.council.domain.ticket.model.entity.TicketEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketEventRepository extends JpaRepository<TicketEvent, Long> {
}
