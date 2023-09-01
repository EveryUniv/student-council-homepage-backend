package com.dku.council.domain.homebus.repository;

import com.dku.council.domain.homebus.model.HomeBusStatus;
import com.dku.council.domain.admin.dto.CancelApprovalTicketsDto;
import com.dku.council.domain.homebus.model.HomeBusStatus;
import com.dku.council.domain.homebus.model.entity.HomeBus;
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
     * 발급된 티켓 + 요청한 티켓 수 + 취소 요청이 진행중인 티켓 수
     * 위를 제외한 나머지가 현재 남아 있는 좌석 수
     */
    @Query("select count(*) from HomeBusTicket " +
            "where bus.id = :busId and (status = 'NEED_APPROVAL' or status = 'ISSUED' or status = 'NEED_CANCEL_APPROVAL')")
    Long countRequestedSeats(@Param("busId") Long busId);

    List<HomeBusTicket> findByBusAndStatus(HomeBus bus, HomeBusStatus status);

    @Query("select " +
            "new com.dku.council.domain.admin.dto.CancelApprovalTicketsDto(t.id, t.user.name, t.bus.label, t.status, c.depositor, c.accountNum, c.bankName, c.createdAt, c.lastModifiedAt) " +
            "from HomeBusTicket t " +
            "join HomeBusCancelRequest c " +
            "on c.ticket.id = t.id " +
            "where t.bus = :bus and t.status = 'NEED_CANCEL_APPROVAL'")
    List<CancelApprovalTicketsDto> getCancelApprovalTicketByBus(HomeBus bus);

    List<HomeBusTicket> findAllByBusIdAndStatus(Long busId, HomeBusStatus status);
}
