package com.dku.council.domain.homebus.repository;

import com.dku.council.domain.homebus.model.entity.HomeBusTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeBusTicketRepository extends JpaRepository<HomeBusTicket, Long> {
    List<HomeBusTicket> findByBusId(Long busId);
}
