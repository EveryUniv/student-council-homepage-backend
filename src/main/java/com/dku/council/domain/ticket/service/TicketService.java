package com.dku.council.domain.ticket.service;

import com.dku.council.domain.ticket.exception.AfterTicketPeriodException;
import com.dku.council.domain.ticket.exception.BeforeTicketPeriodException;
import com.dku.council.domain.ticket.exception.NoTicketException;
import com.dku.council.domain.ticket.model.dto.TicketEventDto;
import com.dku.council.domain.ticket.model.dto.response.ResponseTicketTurnDto;
import com.dku.council.domain.ticket.model.entity.Ticket;
import com.dku.council.domain.ticket.repository.TicketMemoryRepository;
import com.dku.council.domain.ticket.repository.TicketRepository;
import com.dku.council.domain.user.exception.NotAttendingException;
import com.dku.council.domain.user.model.AcademicStatus;
import com.dku.council.domain.user.model.UserInfo;
import com.dku.council.domain.user.service.UserInfoService;
import com.dku.council.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository persistenceRepository;
    private final TicketMemoryRepository memoryRepository;
    private final TicketEventService ticketEventService;
    private final UserInfoService userInfoService;

    @Transactional(readOnly = true)
    public ResponseTicketTurnDto myReservationOrder(Long userId, Long ticketEventId) {
        int turn = memoryRepository.getMyTicket(userId, ticketEventId);

        if (turn == -1) {
            Ticket ticket = persistenceRepository.findByUserIdAndEventId(userId, ticketEventId)
                    .orElseThrow(NoTicketException::new);
            turn = memoryRepository.saveMyTicket(userId, ticketEventId, ticket.getTurn());
        }

        return new ResponseTicketTurnDto(turn);
    }

    public ResponseTicketTurnDto enroll(Long userId, Long ticketEventId, Instant now) {
        TicketEventDto event = ticketEventService.findEventById(ticketEventId);
        Instant eventFrom = DateUtil.toInstant(event.getFrom());
        Instant eventTo = DateUtil.toInstant(event.getTo());

        if (now.isBefore(eventFrom)) {
            throw new BeforeTicketPeriodException();
        }
        
        if (now.isAfter(eventTo)) {
            throw new AfterTicketPeriodException();
        }

        UserInfo userInfo = userInfoService.getUserInfo(userId);
        if (!userInfo.getAcademicStatus().equals(AcademicStatus.ATTENDING.getLabel())) {
            throw new NotAttendingException();
        }

        Duration expiresNextKeyAfter = Duration.between(now, eventTo).plusMinutes(30);
        int turn = memoryRepository.enroll(userId, ticketEventId, expiresNextKeyAfter);
        return new ResponseTicketTurnDto(turn);
    }
}
