package com.dku.council.domain.homebus.service;

import com.dku.council.domain.homebus.model.dto.HomeBusDto;
import com.dku.council.domain.homebus.model.dto.RequestCancelTicketDto;
import com.dku.council.domain.homebus.model.dto.TicketDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeBusUserService {

    public List<HomeBusDto> listBus() {
        return List.of();
    }

    public TicketDto getTicket(Long userId, Long busId) {
        return null;
    }

    public void createTicket(Long userId, Long busId) {

    }

    public void deleteTicket(Long userId, Long busId, RequestCancelTicketDto dto) {

    }
}
