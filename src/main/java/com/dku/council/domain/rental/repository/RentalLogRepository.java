package com.dku.council.domain.rental.repository;

import com.dku.council.domain.rental.model.entity.RentalLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalLogRepository extends JpaRepository<RentalLog, Long> {
}
