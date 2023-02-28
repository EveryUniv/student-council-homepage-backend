package com.dku.council.domain.post.repository;

import com.dku.council.domain.post.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

// TODO 단건조회시 한방 쿼리로 가져오기
@NoRepositoryBean
public interface GenericPostRepository<T extends Post> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
}