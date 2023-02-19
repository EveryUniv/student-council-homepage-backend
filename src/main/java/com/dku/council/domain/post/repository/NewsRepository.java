package com.dku.council.domain.post.repository;

import com.dku.council.domain.post.entity.posttype.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {

}
