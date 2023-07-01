package com.dku.council.domain.homebus.repository;

import com.dku.council.domain.homebus.model.entity.HomeBus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeBusRepository extends JpaRepository<HomeBus, Long> {
}
