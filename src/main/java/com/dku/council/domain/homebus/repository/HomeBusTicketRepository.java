package com.dku.council.domain.homebus.repository;

import com.dku.council.domain.homebus.model.entity.HomeBusTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HomeBusTicketRepository extends JpaRepository<HomeBusTicket, Long> {
    Optional<HomeBusTicket> findByUserIdAndBusId(Long userId, Long busId);

    List<HomeBusTicket> findAllByUserId(Long userId);

    List<HomeBusTicket> findByBusId(Long busId);
}
