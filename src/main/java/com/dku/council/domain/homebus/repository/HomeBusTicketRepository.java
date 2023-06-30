package com.dku.council.domain.homebus.repository;

import com.dku.council.domain.homebus.model.entity.HomeBusTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeBusTicketRepository extends JpaRepository<HomeBusTicket, Long> {
}
