package com.dku.council.domain.tag.repository;

import com.dku.council.domain.tag.model.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
}
