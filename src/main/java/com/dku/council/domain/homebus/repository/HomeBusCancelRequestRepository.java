package com.dku.council.domain.homebus.repository;

import com.dku.council.domain.homebus.model.HomeBusStatus;
import com.dku.council.domain.homebus.model.entity.HomeBusCancelRequest;
import com.dku.council.domain.homebus.model.entity.HomeBusTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HomeBusCancelRequestRepository extends JpaRepository<HomeBusCancelRequest, Long> {
    Optional<HomeBusCancelRequest> findByTicket(HomeBusTicket ticket);

    List<HomeBusCancelRequest> findAllByTicketIn(List<HomeBusTicket> ticketList);

    void deleteByTicket(HomeBusTicket ticket);
}
