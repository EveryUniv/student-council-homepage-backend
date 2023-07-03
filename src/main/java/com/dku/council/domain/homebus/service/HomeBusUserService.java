package com.dku.council.domain.homebus.service;

import com.dku.council.domain.homebus.model.dto.HomeBusDto;
import com.dku.council.domain.homebus.model.dto.RequestCancelTicketDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeBusUserService {

    public List<HomeBusDto> listBus() {
        return List.of();
    }

    @Transactional
    public void createTicket(Long userId, Long busId) {
    }

    @Transactional
    public void deleteTicket(Long userId, Long busId, RequestCancelTicketDto dto) {
    }
}
