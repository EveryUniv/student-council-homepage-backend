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
import com.dku.council.domain.user.model.Campus;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.domain.user.service.UserCampusService;
import com.dku.council.global.error.exception.UserNotFoundException;
import com.dku.council.infra.nhn.service.MMSService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeBusUserService {

    private final UserRepository userRepository;
    private final UserCampusService userCampusService;
    private final HomeBusRepository busRepository;
    private final HomeBusTicketRepository ticketRepository;
    private final HomeBusCancelRequestRepository cancelRepository;
    private final RedissonClient redissonClient;

    private final MMSService mmsService;
    private final MessageSource messageSource;


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

    @Transactional
    public void createTicket(Long userId, Long busId) {
        RLock lock = redissonClient.getLock("homebus:" + busId + ":lock");
        try {
            if (lock != null && !lock.tryLock(20, 3, TimeUnit.SECONDS))
                throw new RuntimeException("It waited for 20 seconds, but can't acquire lock");

            User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
            HomeBus bus = busRepository.findById(busId).orElseThrow(HomeBusNotFoundException::new);
            Long seats = ticketRepository.countRequestedSeats(busId);


            // 중복 티켓 신청 필터링
            if(!ticketRepository.findAllByUserId(userId).isEmpty()) {
                //이미 한 번 이상 취소했다가 다시 신청하는 경우 검증 로직
                if(ticketRepository.findAllByUserId(userId).stream().anyMatch(ticket -> ticket.getStatus().equals(HomeBusStatus.CANCELLED))){
                    checkRemainingSeats(bus, seats);
                    ticketRepository.findAllByUserId(userId).forEach(ticket -> {
                        HomeBusCancelRequest request = cancelRepository.findByTicket(ticket).orElseThrow(HomeBusTicketNotFoundException::new);
                        request.changeTicketToDummy();
                        ticketRepository.delete(ticket);

                        HomeBusTicket newTicket = HomeBusTicket.builder()
                                .bus(bus)
                                .user(user)
                                .status(HomeBusStatus.NEED_APPROVAL)
                                .build();
                        ticketRepository.save(newTicket);
                        String phoneNumber = getUserPhoneNumber(newTicket);
                        sendHomeBusSMS(phoneNumber);
                    });
                    //승인 대기 상태에서 입금을 하지 않아 상태가 NONE으로 된 경우
                }else if(ticketRepository.findAllByUserId(userId).stream().anyMatch(ticket -> ticket.getStatus().equals(HomeBusStatus.NONE))){
                    checkRemainingSeats(bus, seats);
                    ticketRepository.findAllByUserId(userId).forEach(ticket -> {
                        ticket.setStatusToNeedApproval();
                        ticket.setNewBus(bus);
                        String phoneNumber = getUserPhoneNumber(ticket);
                        sendHomeBusSMS(phoneNumber);
                        ticketRepository.save(ticket);
                    });
                }else{
                    throw new AlreadyHomeBusIssuedException();
                }
            }else{
                // 죽전 캠퍼스만 신청 가능
                if (userCampusService.getUserCampus(user) != Campus.JUKJEON) {
                    throw new NotJukjeonException();
                }

                // 자리가 남아야만 신청 가능
                checkRemainingSeats(bus, seats);

                HomeBusTicket ticket = HomeBusTicket.builder()
                        .bus(bus)
                        .user(user)
                        .status(HomeBusStatus.NEED_APPROVAL)
                        .build();
                ticketRepository.save(ticket);
                String phoneNumber = getUserPhoneNumber(ticket);
                sendHomeBusSMS(phoneNumber);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    private static void checkRemainingSeats(HomeBus bus, Long seats) {
        if (bus.getTotalSeats() <= seats) {
            throw new FullSeatsException(bus.getTotalSeats());
        }
    }

    private String getUserPhoneNumber(HomeBusTicket ticket) {
        return ticket.getUser().getPhone().trim().replaceAll("-","");
    }

    @Transactional
    public void deleteTicket(Long userId, Long busId, RequestCancelTicketDto dto) {
        HomeBusTicket ticket = ticketRepository.findByUserIdAndBusId(userId, busId)
                .orElseThrow(HomeBusTicketNotFoundException::new);

        // 승인 단계에서 취소 요청 필터링
        if(ticketRepository.findAllByUserId(userId).stream().anyMatch(busTicket -> busTicket.getStatus().equals(HomeBusStatus.NEED_APPROVAL))) {
            throw new HomeBusTicketStatusException("This request is only available for tickets with \"ISSUED\" status");
        }

        // 중복 취소 요청 필터링
        if (ticketRepository.findAllByUserId(userId).stream().anyMatch(busTicket -> busTicket.getStatus().equals(HomeBusStatus.NEED_CANCEL_APPROVAL))) {
            throw new AlreadyHomeBusCancelRequestException();
        }

        HomeBusCancelRequest req = HomeBusCancelRequest.builder()
                .ticket(ticket)
                .busId(busId)
                .accountNum(dto.getAccountNum())
                .bankName(dto.getBankName())
                .depositor(dto.getDepositor())
                .build();
        cancelRepository.save(req);

        ticket.requestCancel();
    }

    private void sendHomeBusSMS(String phoneNumber){
        Locale locale = LocaleContextHolder.getLocale();
        mmsService.sendMMS(messageSource.getMessage("mms.homebus.title", new Object[]{}, locale), phoneNumber, messageSource.getMessage("mms.homebus.apply-message", new Object[]{}, locale));
    }
}
