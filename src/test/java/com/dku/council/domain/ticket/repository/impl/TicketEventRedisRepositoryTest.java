package com.dku.council.domain.ticket.repository.impl;

import com.dku.council.domain.ticket.model.dto.TicketEventDto;
import com.dku.council.domain.ticket.model.entity.TicketEvent;
import com.dku.council.mock.TicketEventMock;
import com.dku.council.util.base.AbstractContainerRedisTest;
import com.dku.council.util.test.FullIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@FullIntegrationTest
class TicketEventRedisRepositoryTest extends AbstractContainerRedisTest {

    @Autowired
    private TicketEventRedisRepository repository;

    private final TicketEvent event = TicketEventMock.createDummy(1L, "name");
    private final TicketEvent event2 = TicketEventMock.createDummy(2L, "name2");

    @Test
    @DisplayName("이벤트 목록 캐싱")
    void findAllEvents() {
        // when
        int prevSize = repository.findAll().size();
        List<TicketEventDto> savedEvents = repository.saveAll(List.of(event, event2));
        List<TicketEventDto> events = repository.findAll();

        // then
        assertThat(prevSize).isEqualTo(0);
        assertThat(savedEvents).containsExactlyInAnyOrderElementsOf(events);
    }

    @Test
    @DisplayName("단건 저장 및 조회")
    void save() {
        // when
        TicketEventDto saved = repository.save(event);
        TicketEventDto saved2 = repository.save(event2);
        TicketEventDto found = repository.findById(saved.getId()).orElseThrow();
        TicketEventDto found2 = repository.findById(saved2.getId()).orElseThrow();
        TicketEventDto found3 = repository.findById(-1L).orElse(null);

        // then
        assertThat(saved).isEqualTo(found);
        assertThat(saved2).isEqualTo(found2);
        assertThat(found3).isNull();
    }

    @Test
    @DisplayName("삭제")
    void delete() {
        // when
        List<TicketEventDto> saved = repository.saveAll(List.of(event, event2));
        repository.deleteById(event.getId());
        TicketEventDto found = repository.findById(event.getId()).orElse(null);
        TicketEventDto found2 = repository.findById(event2.getId()).orElseThrow();

        // then
        assertThat(found).isNull();
        assertThat(found2).isEqualTo(saved.get(1));
    }
}