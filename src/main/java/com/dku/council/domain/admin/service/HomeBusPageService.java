package com.dku.council.domain.admin.service;

import com.dku.council.domain.admin.dto.CancelApprovalTicketsDto;
import com.dku.council.domain.admin.dto.HomeBusPageDto;
import com.dku.council.domain.admin.dto.request.RequestCreateHomeBusDto;
import com.dku.council.domain.homebus.exception.AlreadyHomeBusIssuedException;
import com.dku.council.domain.homebus.exception.HomeBusNotFoundException;
import com.dku.council.domain.homebus.exception.HomeBusTicketNotFoundException;
import com.dku.council.domain.homebus.exception.HomeBusTicketStatusException;
import com.dku.council.domain.homebus.model.HomeBusStatus;
import com.dku.council.domain.homebus.model.entity.HomeBus;
import com.dku.council.domain.homebus.model.entity.HomeBusTicket;
import com.dku.council.domain.homebus.repository.HomeBusCancelRequestRepository;
import com.dku.council.domain.homebus.repository.HomeBusRepository;
import com.dku.council.domain.homebus.repository.HomeBusTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HomeBusPageService {
    private final HomeBusRepository homeBusRepository;
    private final HomeBusTicketRepository homeBusTicketRepository;
    private final HomeBusCancelRequestRepository homeBusCancelRequestRepository;

    public void create(RequestCreateHomeBusDto dto) {
        HomeBus homeBus = dto.toEntity();
        homeBusRepository.save(homeBus);
    }

    public void update(Long id, RequestCreateHomeBusDto dto) {
        HomeBus homeBus = homeBusRepository.findById(id).orElseThrow(HomeBusNotFoundException::new);
        // todo : redis caching data 필요. 전체 좌석 개수를 잔여석보다 적게 설정하는 것은 불가능하다.
        homeBus.update(dto.getLabel(), dto.getPath(), dto.getDestination(), dto.getTotalSeats());
    }

    public void delete(Long id) {
        Long ticketCount = homeBusTicketRepository.countByBusId(id);
        if (ticketCount == 0) {
            HomeBus homeBus = homeBusRepository.findById(id).orElseThrow(HomeBusNotFoundException::new);
            homeBusRepository.delete(homeBus);
        } else {
            throw new AlreadyHomeBusIssuedException();
        }
    }

    public List<HomeBusPageDto> getAllHomeBus() {
        List<HomeBusPageDto> list = homeBusRepository.getAllHomeBusWithNeedApprovalCnt();
        return list;
    }

    public HomeBus getHomeBusById(Long busId){
        return homeBusRepository.findById(busId).orElseThrow(HomeBusNotFoundException::new);
    }

    public List<HomeBusTicket> getTicketsByBusAndStatus(HomeBus bus, HomeBusStatus status) {
        if(status == null) status = HomeBusStatus.NEED_APPROVAL;
        return homeBusTicketRepository.findByBusAndStatus(bus, status);
    }

    public List<CancelApprovalTicketsDto> getCancelApprovalTicketByBus(HomeBus bus) {
        return homeBusTicketRepository.getCancelApprovalTicketByBus(bus);
    }

    public void approvalOrCancleByTicketStatus(Long ticketId) {
        HomeBusTicket homeBusTicket = homeBusTicketRepository.findById(ticketId).orElseThrow(HomeBusTicketNotFoundException::new);

        if (homeBusTicket.getStatus() == HomeBusStatus.NEED_APPROVAL) {
            homeBusTicket.approvalTicket();
            return;
        }
        if (homeBusTicket.getStatus() == HomeBusStatus.ISSUED) {
            homeBusTicket.setStatusToNeedApproval();
            return;
        }

        throw new HomeBusTicketStatusException("This request is only available for tickets with \"NEED_APPROVAL\", \"ISSUED\" status");
    }


    public void cancelTicket(Long ticketId) {
        HomeBusTicket ticket = homeBusTicketRepository.findById(ticketId).orElseThrow(HomeBusTicketNotFoundException::new);

        if (ticket.getStatus() != HomeBusStatus.NEED_CANCEL_APPROVAL)
            throw new HomeBusTicketStatusException("This request is only available for tickets with \"NEED_CANCEL_APPROVAL\" status");

        homeBusCancelRequestRepository.deleteByTicket(ticket);
        homeBusTicketRepository.delete(ticket);

        return;
    }
}
