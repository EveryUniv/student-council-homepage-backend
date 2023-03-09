package com.dku.council.domain.rental.repository;

import com.dku.council.domain.rental.model.entity.RentalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RentalItemRepository extends JpaRepository<RentalItem, Long>, JpaSpecificationExecutor<RentalItem> {
}
