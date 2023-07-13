package com.dku.council.domain.admin.service;

import com.dku.council.domain.admin.dto.request.RequestCreateHomeBusDto;
import com.dku.council.domain.homebus.exception.AlreadyHomeBusIssuedException;
import com.dku.council.domain.homebus.exception.HomeBusNotFoundException;
import com.dku.council.domain.homebus.exception.InvalidTicketApprovalException;
import com.dku.council.domain.homebus.model.HomeBusStatus;
import com.dku.council.domain.homebus.model.entity.HomeBus;
import com.dku.council.domain.homebus.model.entity.HomeBusTicket;
import com.dku.council.domain.homebus.repository.HomeBusRepository;
import com.dku.council.domain.homebus.repository.HomeBusTicketRepository;
import com.dku.council.domain.user.model.SMSAuth;
import com.dku.council.infra.nhn.service.SMSService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class HomeBusPageService {
    private final HomeBusRepository homeBusRepository;
    private final HomeBusTicketRepository homeBusTicketRepository;
    private final SMSService smsService;
    private final MessageSource messageSource;

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


    /**
     * 해당 버스의 상태 값에 따른 리스트를 반환한다.
     * @param busId     busId
     * @param status    NONE, NEED_APPROVAL, ISSUED, NEED_CANCEL_APPROVAL
     * @return
     */
    public List<HomeBusTicket> getHomeBusTicketList(Long busId, HomeBusStatus status) {
        return homeBusTicketRepository.findAllByBusIdAndStatus(busId, status);
    }

    /**
     * 여러 티켓 id를 입력받고 해당 티켓들을 승인한다.
     * @param ticketIds : [1, 2, 3, 4, 5]
     * @param adminName : "관리자 이름"
     */
    public void issue(List<Long> ticketIds, String adminName) {
        List<HomeBusTicket> homeBusTickets = homeBusTicketRepository.findAllById(ticketIds);
        // 승인 대기 상태가 아닌 티켓이 포함되어 있으면 예외를 발생시킨다.
        homeBusTickets.stream().filter(ticket -> ticket.getStatus() != HomeBusStatus.NEED_APPROVAL).findAny().ifPresent(ticket -> {
            throw new InvalidTicketApprovalException();
        });
        homeBusTickets.forEach(ticket -> ticket.issue(adminName));
        // ex) x호차(대구 - 부산) 노선 신청이 완료되었습니다. 총학생회 신청 홈페이지에서 승차권 확인 부탁드립니다. 탑승 당일 원활한 진행을 위해 스태프에게 승차권을 제시해주세요.
        homeBusTickets.forEach(ticket -> {
            String phoneNumber = ticket.getUser().getPhone().trim().replaceAll("-", "");
            String label = ticket.getBus().getLabel();
            String path = ticket.getBus().getPath().replaceAll(",", " - ");
            String destination = ticket.getBus().getDestination();
            Locale locale = LocaleContextHolder.getLocale();
            smsService.sendSMS(phoneNumber, messageSource.getMessage("sms.homebus.approve-message", new Object[]{label, path, destination}, locale));
        });
    }



}
