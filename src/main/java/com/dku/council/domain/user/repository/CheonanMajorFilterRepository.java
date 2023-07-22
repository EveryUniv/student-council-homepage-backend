package com.dku.council.domain.user.repository;

import com.dku.council.domain.user.model.entity.CheonanMajorFilter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheonanMajorFilterRepository extends JpaRepository<CheonanMajorFilter, Long> {
    Long countByFilter(String filter);
}
