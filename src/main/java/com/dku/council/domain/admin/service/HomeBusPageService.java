package com.dku.council.domain.admin.service;

import com.dku.council.domain.admin.dto.CancelApprovalTicketsDto;
import com.dku.council.domain.admin.dto.HomeBusPageDto;
import com.dku.council.domain.admin.dto.request.RequestCreateHomeBusDto;
import com.dku.council.domain.homebus.exception.*;
import com.dku.council.domain.homebus.model.HomeBusStatus;
import com.dku.council.domain.homebus.exception.AlreadyHomeBusIssuedException;
import com.dku.council.domain.homebus.exception.HomeBusNotFoundException;
import com.dku.council.domain.homebus.exception.HomeBusTicketNotFoundException;
import com.dku.council.domain.homebus.exception.HomeBusTicketStatusException;
import com.dku.council.domain.homebus.model.entity.HomeBus;
import com.dku.council.domain.homebus.model.entity.HomeBusCancelRequest;
import com.dku.council.domain.homebus.model.entity.HomeBusTicket;
import com.dku.council.domain.homebus.repository.HomeBusCancelRequestRepository;
import com.dku.council.domain.homebus.repository.HomeBusRepository;
import com.dku.council.domain.homebus.repository.HomeBusTicketRepository;
import com.dku.council.domain.homebus.service.HomeBusUserService;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import com.dku.council.infra.nhn.service.MMSService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class HomeBusPageService {
    private final HomeBusRepository homeBusRepository;
    private final HomeBusTicketRepository homeBusTicketRepository;
    private final HomeBusUserService homeBusUserService;
    private final HomeBusCancelRequestRepository homeBusCancelRequestRepository;
    private final UserRepository userRepository;


    private final MMSService mmsService;
    private final MessageSource messageSource;


    public void create(RequestCreateHomeBusDto dto) {
        HomeBus homeBus = dto.toEntity();
        homeBusRepository.save(homeBus);
    }

    public void update(Long id, RequestCreateHomeBusDto dto) {
        HomeBus homeBus = homeBusRepository.findById(id).orElseThrow(HomeBusNotFoundException::new);
        // todo : redis caching data 필요. 전체 좌석 개수를 잔여석보다 적게 설정하는 것은 불가능하다.
        long remainingSeats = homeBus.getTotalSeats() - homeBusTicketRepository.countRequestedSeats(id);
        if(dto.getTotalSeats() < remainingSeats) {
            throw new ExceedLimitException(String.valueOf(remainingSeats));
        }
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

    /**
     * 해당 버스의 상태 값에 따른 리스트를 반환한다.
     * @param busId     busId
     * @param status    NONE, NEED_APPROVAL, ISSUED, NEED_CANCEL_APPROVAL, CANCELED
     * @return
     */
    @Deprecated
    public List<HomeBusTicket> getHomeBusTicketList(Long busId, HomeBusStatus status) {
        return homeBusTicketRepository.findAllByBusIdAndStatus(busId, status);
    }

    /**
     * 취소 신청을 한 사람들의 정보를 반환한다.
     * @param busId   busId
     * @param status  NEED_CANCEL_APPROVAL, CANCELED
     * @return
     */
    @Deprecated
    public List<HomeBusCancelRequest> getHomeBusCancelRequestList(Long busId, HomeBusStatus status) {
        List<HomeBusTicket> ticketList = homeBusTicketRepository.findAllByBusIdAndStatus(busId, status);
        return homeBusCancelRequestRepository.findAllByTicketIn(ticketList);
    }

    /**
     * 해당 API는 계좌로 입금이 이루어진 뒤 실행되어야 합니다.
     * 여러 티켓 id를 입력받고 해당 티켓들을 승인한다.
     * @param ticketIds : [1, 2, 3, 4, 5]
     * @param adminName : "관리자 이름"
     */
    @Deprecated
    public void issue(List<Long> ticketIds, String adminName) {
        List<HomeBusTicket> homeBusTickets = homeBusTicketRepository.findAllById(ticketIds);
        // 승인 대기 상태가 아닌 티켓이 포함되어 있으면 예외를 발생시킨다.
        homeBusTickets.stream().filter(ticket -> ticket.getStatus() != HomeBusStatus.NEED_APPROVAL).findAny().ifPresent(ticket -> {
            throw new InvalidTicketApprovalException();
        });
        // ex) x호차(대구 - 부산) 노선 신청이 완료되었습니다. 총학생회 신청 홈페이지에서 승차권 확인 부탁드립니다. 탑승 당일 원활한 진행을 위해 스태프에게 승차권을 제시해주세요.
        homeBusTickets.forEach(ticket -> {
            String phoneNumber = ticket.getUser().getPhone().trim().replaceAll("-", "");
            sendHomeBusMMS(ticket, phoneNumber);
            ticket.issue(adminName);
        });
    }
    /**
     * 해당 API는 계좌로 환불이 이루어진 뒤 실행되어야 합니다.
     * 여러 티켓 id를 입력받고 해당 티켓들을 취소한다.
     * @param ticketIds : [1, 2, 3, 4, 5]
     * @param adminName : "관리자 이름"
     */
    @Deprecated
    public void cancel(List<Long> ticketIds, String adminName) {
        List<HomeBusTicket> homeBusTickets = homeBusTicketRepository.findAllById(ticketIds);
        //취소 대기 상태가 아닌 티켓이 포함되어 있으면 예외를 발생시킨다.
        homeBusTickets.stream().filter(ticket -> ticket.getStatus() != HomeBusStatus.NEED_CANCEL_APPROVAL).findAny().ifPresent(ticket -> {
            throw new InvalidTicketApprovalException();
        });
        homeBusTickets.forEach(ticket -> {
            Optional<HomeBusCancelRequest> optionalTicket = homeBusCancelRequestRepository.findByTicket(ticket);
            if(optionalTicket.isPresent()){
                HomeBusCancelRequest cancelTicket = optionalTicket.get();
                String depositor = cancelTicket.getDepositor();
                String bank = cancelTicket.getBankName();
                String account = cancelTicket.getAccountNum();
                String phoneNumber = ticket.getUser().getPhone().trim().replaceAll("-", "");
                sendHomeBusCancelMMS(depositor, bank, account, phoneNumber);
                ticket.cancel(adminName);
            }
        });
    }

    /**
     * 강제로 티켓을 발급한다.
     * @param studentId 학번:2103302
     * @param busId     버스 id
     * @param adminName "관리자 이름"
     */
    public void forceIssue(String studentId, Long busId, String adminName){
        User user = userRepository.findByStudentId(studentId).orElseThrow(UserNotFoundException::new);
        homeBusUserService.createTicket(user.getId(), busId);
        HomeBusTicket ticket = homeBusTicketRepository.findByUserIdAndBusId(user.getId(), busId).orElseThrow(HomeBusTicketNotFoundException::new);
        ticket.issue(adminName);
        String phoneNumber = user.getPhone().trim().replaceAll("-", "");
        sendHomeBusMMS(ticket, phoneNumber);
    }

    private void sendHomeBusMMS(HomeBusTicket ticket, String phoneNumber) {
        String label = ticket.getBus().getLabel();
        String path = ticket.getBus().getPath().replaceAll(",", " - ");
        String destination = ticket.getBus().getDestination();
        Locale locale = LocaleContextHolder.getLocale();
        mmsService.sendMMS(messageSource.getMessage("mms.homebus.title", new Object[]{}, locale), phoneNumber,
                messageSource.getMessage("mms.homebus.approve-message", new Object[]{label, path, destination}, locale));
    }



    private void sendHomeBusCancelMMS(String depositor, String bank, String account, String phoneNumber) {
        Locale locale = LocaleContextHolder.getLocale();
        mmsService.sendMMS(messageSource.getMessage("mms.homebus.title", new Object[]{}, locale), phoneNumber,
                messageSource.getMessage("mms.homebus.cancel-message", new Object[]{depositor, bank, account}, locale));
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

    public void approvalOrCancleByTicketStatus(Long ticketId, Long adminId) {
        HomeBusTicket homeBusTicket = homeBusTicketRepository.findById(ticketId).orElseThrow(HomeBusTicketNotFoundException::new);
        User admin = userRepository.findById(adminId).orElseThrow(UserNotFoundException::new);
        if (homeBusTicket.getStatus() == HomeBusStatus.NEED_APPROVAL) {
            homeBusTicket.approvalTicket(admin.getName());
            sendHomeBusMMS(homeBusTicket);
            return;
        }
        if (homeBusTicket.getStatus() == HomeBusStatus.ISSUED) {
            homeBusTicket.setStatusToNeedApproval();
            return;
        }

        throw new HomeBusTicketStatusException("This request is only available for tickets with \"NEED_APPROVAL\", \"ISSUED\" status");
    }

    private void sendHomeBusMMS(HomeBusTicket ticket) {
        String phoneNumber = ticket.getUser().getPhone().trim().replaceAll("-", "");

        String label = ticket.getBus().getLabel();
        String path = ticket.getBus().getPath().replaceAll(",", " - ");
        String destination = ticket.getBus().getDestination();
        Locale locale = LocaleContextHolder.getLocale();

        mmsService.sendMMS(messageSource.getMessage("mms.homebus.title", new Object[]{}, locale), phoneNumber,
                messageSource.getMessage("mms.homebus.approve-message", new Object[]{label, path, destination}, locale));
    }

    public void cancelTicket(Long ticketId, Long userId) {
        HomeBusTicket ticket = homeBusTicketRepository.findById(ticketId).orElseThrow(HomeBusTicketNotFoundException::new);
        User admin = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ticket.cancel(admin.getName());

        Optional<HomeBusCancelRequest> optionalTicket = homeBusCancelRequestRepository.findByTicket(ticket);

        if(optionalTicket.isPresent()){
            HomeBusCancelRequest cancelTicket = optionalTicket.get();

            sendHomeBusCancelMMS(cancelTicket);
        }

        return;
    }
    private void sendHomeBusCancelMMS(HomeBusCancelRequest cancelTicket) {
        String depositor = cancelTicket.getDepositor();
        String bank = cancelTicket.getBankName();
        String account = cancelTicket.getAccountNum();
        String phoneNumber = cancelTicket.getTicket().getUser().getPhone().trim().replaceAll("-", "");
        Locale locale = LocaleContextHolder.getLocale();
        mmsService.sendMMS(messageSource.getMessage("mms.homebus.title", new Object[]{}, locale), phoneNumber,
                messageSource.getMessage("mms.homebus.cancel-message", new Object[]{depositor, bank, account}, locale));
    }

    /**
     * 승인 대기 중인 티켓을 취소한다.
     * 입금자가 정해진 시간까지 입금이 되지 않으면 실행되어야 합니다.
     * @param ticketId : ticketId
     */
    public void cancelNeedApprovalTicket(Long ticketId) {
        HomeBusTicket ticket = homeBusTicketRepository.findById(ticketId).orElseThrow(HomeBusTicketNotFoundException::new);

        //티켓이 승인 대기 중인 상태인지 확인합니다.
        if(ticket.getStatus() == HomeBusStatus.NEED_APPROVAL){
            ticket.setStatusToNone();
            sendHomeBusApprovalCancelMMS(ticket);
            return;
        }

        throw new HomeBusTicketStatusException("This request is only available for tickets with \"NEED_APPROVAL\" status");
    }

    private void sendHomeBusApprovalCancelMMS(HomeBusTicket ticket) {
        String phoneNumber = ticket.getUser().getPhone().trim().replaceAll("-", "");
        String userName = ticket.getUser().getName();
        String applyDate = ticket.getCreatedAt().format(DateTimeFormatter.ofPattern("MM월 dd일 HH시mm분"));

        Locale locale = LocaleContextHolder.getLocale();
        mmsService.sendMMS(messageSource.getMessage(
                "mms.homebus.title", new Object[]{}, locale), phoneNumber,
                messageSource.getMessage("mms.homebus.need-approval.cancel-message", new Object[]{userName, applyDate}, locale));
    }
}
