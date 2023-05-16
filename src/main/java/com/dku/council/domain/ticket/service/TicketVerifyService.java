package com.dku.council.domain.ticket.service;

import com.dku.council.domain.ticket.exception.NoTicketException;
import com.dku.council.domain.ticket.model.TicketStatus;
import com.dku.council.domain.ticket.model.dto.response.ResponseManagerTicketDto;
import com.dku.council.domain.ticket.model.dto.response.ResponseTicketDto;
import com.dku.council.domain.ticket.model.entity.Ticket;
import com.dku.council.domain.ticket.repository.TicketRepository;
import com.dku.council.domain.user.model.UserInfo;
import com.dku.council.domain.user.service.UserInfoService;
import com.dku.council.domain.user.util.CodeGenerator;
import com.dku.council.infra.nhn.service.SMSService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * 티켓 검증 서비스.
 * 티켓은 참여 대상자로 확정된 사람의 티켓만 검증할 수 있습니다. (DB에 있으면서, 선정자인 경우)
 */
@Service
@RequiredArgsConstructor
public class TicketVerifyService {

    private final TicketRepository persistenceRepository;
    private final UserInfoService userInfoService;
    private final SMSService smsService;
    private final MessageSource messageSource;

    @Value("${app.ticket.auth-digit-count}")
    private final int digitCount;

    @Value("${app.ticket.auto-send-sms}")
    private final boolean autoSendSms;

    @Transactional(readOnly = true)
    public ResponseTicketDto myTicket(Long userId, Long eventId) {
        Ticket ticket = persistenceRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(NoTicketException::new);

        if (ticket.getStatus().hasNoTicket()) {
            throw new NoTicketException();
        }

        UserInfo userInfo = userInfoService.getUserInfo(userId);
        return makeTicketDto(userInfo, ticket);
    }

    @Transactional(readOnly = true)
    public ResponseManagerTicketDto getTicketInfo(Long ticketId) {
        Ticket ticket = findTicket(ticketId);

        UserInfo userInfo = userInfoService.getUserInfo(ticket.getUser().getId());
        ResponseTicketDto dto = makeTicketDto(userInfo, ticket);

        Long eventId = ticket.getEvent().getId();

        String authCode = "";
        if (ticket.getStatus() == TicketStatus.ISSUABLE) {
            if (autoSendSms) {
                authCode = sendSms(userInfo.getPhone());
            } else {
                authCode = CodeGenerator.generateDigitCode(digitCount);
            }
        }

        return new ResponseManagerTicketDto(dto, authCode, eventId);
    }

    private ResponseTicketDto makeTicketDto(UserInfo userInfo, Ticket ticket) {
        boolean issued = ticket.getStatus() == TicketStatus.ISSUED;
        String majorName = userInfo.getMajor().getName();

        return new ResponseTicketDto(ticket.getId(), userInfo.getName(),
                majorName, userInfo.getStudentId(), issued, ticket.getTurn());
    }

    private String sendSms(String phoneNumber) {
        String code = CodeGenerator.generateDigitCode(digitCount);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("sms.ticket-auth-message", new Object[]{code}, locale);

        smsService.sendSMS(phoneNumber, message);
        return code;
    }

    @Transactional(readOnly = true)
    public String resendSms(Long ticketId) {
        Ticket ticket = findTicket(ticketId);

        UserInfo userInfo = userInfoService.getUserInfo(ticket.getUser().getId());
        return sendSms(userInfo.getPhone());
    }

    @Transactional
    public void setToIssued(Long ticketId) {
        Ticket ticket = findTicket(ticketId);
        ticket.markAsIssued();
    }

    @Transactional
    public void setToUnissued(Long ticketId) {
        Ticket ticket = findTicket(ticketId);
        ticket.markAsIssuable();
    }

    private Ticket findTicket(Long ticketId) {
        Ticket ticket = persistenceRepository.findById(ticketId).orElseThrow(NoTicketException::new);
        if (ticket.getStatus().hasNoTicket()) {
            throw new NoTicketException();
        }
        return ticket;
    }
}
