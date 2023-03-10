package com.dku.council.domain.rental.repository;

import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long>, JpaSpecificationExecutor<Rental> {
    @Query("select r from Rental r where r.user=:user and r.item=:item and r.isActive=true")
    Optional<Rental> findByUserAndItem(User user, RentalItem item);

    @Query("select r from Rental r where r.id=:id and r.isActive=true")
    Optional<Rental> findById(Long id);
}
