package com.dku.council.domain.homebus.service;

import com.dku.council.domain.homebus.exception.HomeBusNotFoundException;
import com.dku.council.domain.homebus.exception.HomeBusTicketNotFoundException;
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


    public List<HomeBusDto> listBus(Long userId) {
        // TODO 잔여석
        Map<Long, HomeBusStatus> busStatusMap = new HashMap<>();
        for (HomeBusTicket ticket : ticketRepository.findAllByUserId(userId)) {
            busStatusMap.put(ticket.getBus().getId(), ticket.getStatus());
        }

        List<HomeBus> buses = busRepository.findAll();
        return buses.stream()
                .map(ent -> {
                    int remainingSeats = 0;
                    HomeBusStatus status = busStatusMap.getOrDefault(ent.getId(), HomeBusStatus.NONE);
                    return new HomeBusDto(ent, remainingSeats, status);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void createTicket(Long userId, Long busId) {
        // TODO 중복 티켓 신청 필터링
        // TODO 죽전 학생/대학원생만 신청할 수 있도록
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        HomeBus bus = busRepository.findById(busId).orElseThrow(HomeBusNotFoundException::new);

        HomeBusTicket ticket = HomeBusTicket.builder()
                .bus(bus)
                .user(user)
                .status(HomeBusStatus.NEED_APPROVAL)
                .build();

        ticketRepository.save(ticket);
    }

    @Transactional
    public void deleteTicket(Long userId, Long busId, RequestCancelTicketDto dto) {
        // TODO 중복 취소 요청 필터링
        HomeBusTicket ticket = ticketRepository.findByUserIdAndBusId(userId, busId)
                .orElseThrow(HomeBusTicketNotFoundException::new);

        HomeBusCancelRequest req = HomeBusCancelRequest.builder()
                .ticket(ticket)
                .accountNum(dto.getAccountNum())
                .bankName(dto.getBankName())
                .depositor(dto.getDepositor())
                .build();

        cancelRepository.save(req);
    }
}
