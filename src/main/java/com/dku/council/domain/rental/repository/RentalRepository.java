package com.dku.council.domain.rental.repository;

import com.dku.council.domain.rental.model.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long>, JpaSpecificationExecutor<Rental> {
    @Query("select r from Rental r where r.id=:id and r.isActive=true")
    Optional<Rental> findById(Long id);
}
