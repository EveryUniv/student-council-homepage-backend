package com.dku.council.domain.homebus.repository;

import com.dku.council.domain.homebus.model.entity.HomeBusTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HomeBusTicketRepository extends JpaRepository<HomeBusTicket, Long> {
    Optional<HomeBusTicket> findByBusId(Long busId);
}
