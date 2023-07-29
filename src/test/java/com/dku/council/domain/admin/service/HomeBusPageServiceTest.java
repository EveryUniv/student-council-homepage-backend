package com.dku.council.domain.admin.service;

import com.dku.council.domain.admin.dto.request.RequestCreateHomeBusDto;
import com.dku.council.domain.homebus.exception.AlreadyHomeBusIssuedException;
import com.dku.council.domain.homebus.model.entity.HomeBus;
import com.dku.council.domain.homebus.repository.HomeBusRepository;
import com.dku.council.domain.homebus.repository.HomeBusTicketRepository;
import com.dku.council.mock.HomeBusMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomeBusPageServiceTest {

    @Mock
    private HomeBusRepository homeBusRepository;
    @Mock
    private HomeBusTicketRepository homeBusTicketRepository;

    @InjectMocks
    private HomeBusPageService service;

    @Test
    void update() {
        //todo : 전체 좌석 개수를 잔여석보다 적게 설정하는 것은 불가능하다.
    }

    @Test
    @DisplayName("삭제 실패 - 이미 발급된 홈버스가 있을 때")
    void failedDeleteByAlreadyIssued() {
        //given
        HomeBus homeBus = HomeBusMock.create();
        when(homeBusTicketRepository.countByBusId(homeBus.getId())).thenReturn(1L);

        //when & then
        assertThrows(AlreadyHomeBusIssuedException.class, () -> service.delete(homeBus.getId()));
    }

}