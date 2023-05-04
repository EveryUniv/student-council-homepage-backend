package com.dku.council.domain.ticket.repository;

import com.dku.council.domain.ticket.model.dto.TicketDto;

import java.time.Duration;
import java.util.List;

public interface TicketMemoryRepository {

    /**
     * 티켓을 발급한다.
     *
     * @param userId              사용자 ID
     * @param ticketEventId       티켓 이벤트 ID
     * @param expiresNextKeyAfter 다음 티켓 아이디 캐싱 만료 시간
     * @return 발급된 티켓 순번. 이미 발급한 경우 -1 반환.
     */
    int enroll(Long userId, Long ticketEventId, Duration expiresNextKeyAfter);

    /**
     * 순번을 조회한다.
     *
     * @param userId        사용자 ID
     * @param ticketEventId 티켓 이벤트 ID
     * @return 티켓 순번. 티켓이 발급되지 않은 경우 -1 반환.
     */
    int getMyTicket(Long userId, Long ticketEventId);

    /**
     * 티켓을 캐싱합니다.
     *
     * @param userId        사용자 ID
     * @param ticketEventId 티켓 이벤트 ID
     * @return 티켓 순번.
     */
    int saveMyTicket(Long userId, Long ticketEventId, int turn);

    /**
     * 모든 티켓을 가져오고, 캐시를 비운다.
     *
     * @return 티켓 목록
     */
    List<TicketDto> flushAllTickets();
}
