package com.dku.council.domain.ticket.repository;

import com.dku.council.domain.ticket.model.dto.TicketEventDto;
import com.dku.council.domain.ticket.model.entity.TicketEvent;

import java.util.List;

public interface TicketMemoryRepository {

    /**
     * 캐싱된 티켓 이벤트 목록을 조회한다.
     *
     * @return 캐싱된 티켓 이벤트 목록. 캐싱되지 않은 경우 null 반환.
     */
    List<TicketEventDto> findAllEvents();

    /**
     * 티켓 이벤트 목록을 캐싱한다.
     *
     * @param events 티켓 이벤트 목록 (Entity)
     * @return 캐싱된 티켓 이벤트 목록
     */
    List<TicketEventDto> saveEvents(List<TicketEvent> events);

    /**
     * 티켓을 발급한다.
     *
     * @param userId        사용자 ID
     * @param ticketEventId 티켓 이벤트 ID
     * @return 발급된 티켓 순번. 이미 발급한 경우 -1 반환.
     */
    int enroll(Long userId, Long ticketEventId);

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
}
