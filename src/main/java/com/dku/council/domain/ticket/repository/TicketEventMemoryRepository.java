package com.dku.council.domain.ticket.repository;

import com.dku.council.domain.ticket.model.dto.TicketEventDto;
import com.dku.council.domain.ticket.model.entity.TicketEvent;

import java.util.List;
import java.util.Optional;

public interface TicketEventMemoryRepository {

    /**
     * 캐싱된 티켓 이벤트 목록을 조회한다.
     *
     * @return 캐싱된 티켓 이벤트 목록.
     */
    List<TicketEventDto> findAll();

    /**
     * 티켓 이벤트 목록을 캐싱한다.
     *
     * @param events 티켓 이벤트 목록 (Entity)
     * @return 캐싱된 티켓 이벤트 목록
     */
    List<TicketEventDto> saveAll(List<TicketEvent> events);

    /**
     * 특정 티켓 이벤트를 캐싱한다.
     *
     * @param event 티켓 이벤트 (Entity)
     * @return 캐싱된 티켓 이벤트
     */
    TicketEventDto save(TicketEvent event);

    /**
     * 티켓 이벤트를 ID로 탐색합니다.
     *
     * @param id 티켓 이벤트 ID
     * @return 티켓 이벤트. 존재하지 않는 경우 null 반환.
     */
    Optional<TicketEventDto> findById(Long id);

    /**
     * 티켓 이벤트를 캐시에서 삭제한다.
     *
     * @param id 티켓 이벤트 ID
     */
    void deleteById(Long id);
}
