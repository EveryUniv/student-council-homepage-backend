package com.dku.council.domain.homebus.repository;

import com.dku.council.domain.homebus.model.entity.HomeBusCancelRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeBusCancelRequestRepository extends JpaRepository<HomeBusCancelRequest, Long> {
}
