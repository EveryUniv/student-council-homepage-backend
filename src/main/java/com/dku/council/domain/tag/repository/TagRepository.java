package com.dku.council.domain.tag.repository;

import com.dku.council.domain.tag.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
