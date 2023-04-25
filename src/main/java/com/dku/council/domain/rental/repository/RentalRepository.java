package com.dku.council.domain.rental.repository;

import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long>, JpaSpecificationExecutor<Rental> {
    @Query("select r from Rental r " +
            "where r.user=:user and r.item=:item and r.isActive=true")
    Optional<Rental> findByUserAndItem(@Param("user") User user,
                                       @Param("item") RentalItem item);

    @Query("select r from Rental r " +
            "join fetch r.item " +
            "join fetch r.user " +
            "where r.id=:id and r.isActive=true")
    Optional<Rental> findById(@Param("id") Long id);

    @Query(value = "select r from Rental r " +
            "join fetch r.item " +
            "join fetch r.user " +
            "where r.item.id=:itemId and r.isActive=true",
            countQuery = "select count(r) from Rental r " +
                    "where r.item.id=:itemId and r.isActive=true")
    Page<Rental> findAllByItemId(@Param("itemId") Long itemId, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"user", "item"})
    Page<Rental> findAll(Specification<Rental> spec, Pageable pageable);
}
