package com.dku.council.domain.rental.repository;

import com.dku.council.domain.rental.model.entity.RentalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RentalItemRepository extends JpaRepository<RentalItem, Long>, JpaSpecificationExecutor<RentalItem> {
    @Query("select r from RentalItem r where r.id=:id and r.isActive=true")
    Optional<RentalItem> findById(@Param("id") Long id);

}
