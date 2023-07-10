package com.dku.council.domain.homebus.repository;

import com.dku.council.domain.homebus.model.entity.HomeBusTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HomeBusTicketRepository extends JpaRepository<HomeBusTicket, Long> {
    Optional<HomeBusTicket> findByUserIdAndBusId(Long userId, Long busId);

    List<HomeBusTicket> findAllByUserId(Long userId);

    Long countByBusId(Long busId);

    /**
     * 발급된 티켓 + 요청한 티켓 수
     */
    @Query("select count(*) from HomeBusTicket " +
            "where bus.id = :busId and (status = 'NEED_APPROVAL' or status = 'ISSUED')")
    Long countRequestedSeats(@Param("busId") Long busId);
}
