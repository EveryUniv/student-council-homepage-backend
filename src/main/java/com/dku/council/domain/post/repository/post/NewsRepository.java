package com.dku.council.domain.post.repository.post;

import com.dku.council.domain.post.model.entity.posttype.News;

import java.util.List;

public interface NewsRepository extends GenericPostRepository<News> {
    List<News> findTop5ByOrderByCreatedAtDesc();

}