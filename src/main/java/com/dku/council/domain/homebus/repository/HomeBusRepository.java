package com.dku.council.domain.homebus.repository;

import com.dku.council.domain.admin.dto.HomeBusPageDto;
import com.dku.council.domain.homebus.model.HomeBusStatus;
import com.dku.council.domain.homebus.model.entity.HomeBus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HomeBusRepository extends JpaRepository<HomeBus, Long> {

    @Query("select new com.dku.council.domain.admin.dto.HomeBusPageDto(" +
            "b.id, b.label, b.path, b.destination, b.totalSeats, count(hbt.id)" +
            ") " +
            "from HomeBus b " +
            "left join HomeBusTicket hbt " +
            "on b.id = hbt.bus " +
            "and hbt.status = 'NEED_APPROVAL' " +
            "group by b.id")
    List<HomeBusPageDto> getAllHomeBusWithNeedApprovalCnt();




}
