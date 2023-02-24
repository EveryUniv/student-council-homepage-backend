package com.dku.council.domain.post.repository;

import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GeneralForumRepository extends JpaRepository<GeneralForum, Long>, JpaSpecificationExecutor<GeneralForum> {
}
