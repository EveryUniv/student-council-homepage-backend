package com.dku.council.domain.post.repository;

import com.dku.council.domain.post.model.entity.posttype.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
}
