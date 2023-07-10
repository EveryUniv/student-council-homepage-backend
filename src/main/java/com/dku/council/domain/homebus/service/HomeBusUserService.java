package com.dku.council.domain.homebus.service;

import com.dku.council.domain.homebus.exception.*;
import com.dku.council.domain.homebus.model.HomeBusStatus;
import com.dku.council.domain.homebus.model.dto.HomeBusDto;
import com.dku.council.domain.homebus.model.dto.RequestCancelTicketDto;
import com.dku.council.domain.homebus.model.entity.HomeBus;
import com.dku.council.domain.homebus.model.entity.HomeBusCancelRequest;
import com.dku.council.domain.homebus.model.entity.HomeBusTicket;
import com.dku.council.domain.homebus.repository.HomeBusCancelRequestRepository;
import com.dku.council.domain.homebus.repository.HomeBusRepository;
import com.dku.council.domain.homebus.repository.HomeBusTicketRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeBusUserService {

    private final UserRepository userRepository;
    private final HomeBusRepository busRepository;
    private final HomeBusTicketRepository ticketRepository;
    private final HomeBusCancelRequestRepository cancelRepository;


    @Transactional(readOnly = true)
    public List<HomeBusDto> listBus(Long userId) {
        Map<Long, HomeBusStatus> busStatusMap = new HashMap<>();
        for (HomeBusTicket ticket : ticketRepository.findAllByUserId(userId)) {
            busStatusMap.put(ticket.getBus().getId(), ticket.getStatus());
        }

        List<HomeBus> buses = busRepository.findAll();
        return buses.stream()
                .map(ent -> {
                    long remainingSeats = ent.getTotalSeats() - ticketRepository.countRequestedSeats(ent.getId()); // TODO 캐싱해서 사용하기
                    HomeBusStatus status = busStatusMap.getOrDefault(ent.getId(), HomeBusStatus.NONE);
                    return new HomeBusDto(ent, (int) remainingSeats, status);
                })
                .collect(Collectors.toList());
    }

    // TODO 죽전 학생/대학원생만 신청할 수 있도록
    // TODO 동시성 문제 해결
    @Transactional
    public void createTicket(Long userId, Long busId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        HomeBus bus = busRepository.findById(busId).orElseThrow(HomeBusNotFoundException::new);
        Long seats = ticketRepository.countRequestedSeats(busId);

        // 중복 티켓 신청 필터링
        if (!ticketRepository.findAllByUserId(userId).isEmpty()) {
            throw new AlreadyHomeBusIssuedException();
        }

        // 자리가 남아야만 신청 가능
        if (bus.getTotalSeats() <= seats) {
            throw new FullSeatsException(bus.getTotalSeats());
        }

        HomeBusTicket ticket = HomeBusTicket.builder()
                .bus(bus)
                .user(user)
                .status(HomeBusStatus.NEED_APPROVAL)
                .build();

        ticketRepository.save(ticket);
    }

    @Transactional
    public void deleteTicket(Long userId, Long busId, RequestCancelTicketDto dto) {
        HomeBusTicket ticket = ticketRepository.findByUserIdAndBusId(userId, busId)
                .orElseThrow(HomeBusTicketNotFoundException::new);

        // 중복 취소 요청 필터링
        if (cancelRepository.findByTicket(ticket).isPresent()) {
            throw new AlreadyHomeBusCancelRequestException();
        }

        HomeBusCancelRequest req = HomeBusCancelRequest.builder()
                .ticket(ticket)
                .accountNum(dto.getAccountNum())
                .bankName(dto.getBankName())
                .depositor(dto.getDepositor())
                .build();
        cancelRepository.save(req);
    }
}
