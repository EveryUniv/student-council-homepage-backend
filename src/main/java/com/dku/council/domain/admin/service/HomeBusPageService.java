package com.dku.council.domain.admin.service;

import com.dku.council.domain.admin.dto.request.RequestCreateHomeBusDto;
import com.dku.council.domain.homebus.exception.AlreadyHomeBusIssuedException;
import com.dku.council.domain.homebus.exception.HomeBusNotFoundException;
import com.dku.council.domain.homebus.model.entity.HomeBus;
import com.dku.council.domain.homebus.model.entity.HomeBusTicket;
import com.dku.council.domain.homebus.repository.HomeBusRepository;
import com.dku.council.domain.homebus.repository.HomeBusTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HomeBusPageService {
    private final HomeBusRepository homeBusRepository;
    private final HomeBusTicketRepository homeBusTicketRepository;

    public void create(RequestCreateHomeBusDto dto){
        HomeBus homeBus = dto.toEntity();
        homeBusRepository.save(homeBus);
    }

    public void update(Long id, RequestCreateHomeBusDto dto){
        HomeBus homeBus = homeBusRepository.findById(id).orElseThrow(HomeBusNotFoundException::new);
        // todo : redis caching data 필요. 전체 좌석 개수를 잔여석보다 적게 설정하는 것은 불가능하다.
        homeBus.update(dto.getLabel(), dto.getPath(), dto.getDestination(), dto.getTotalSeats());
    }

    public void delete(Long id){
        List<HomeBusTicket> homeBusTickets = homeBusTicketRepository.findByBusId(id);
        if(homeBusTickets.isEmpty()){
            HomeBus homeBus = homeBusRepository.findById(id).orElseThrow(HomeBusNotFoundException::new);
            homeBusRepository.delete(homeBus);
        }else{
            throw new AlreadyHomeBusIssuedException();
        }
    }
}
