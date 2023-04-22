package com.dku.council.domain.post.repository.post;

import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GeneralForumRepository extends GenericPostRepository<GeneralForum>, JpaSpecificationExecutor<GeneralForum> {

    @Override
    @EntityGraph(attributePaths = {"user", "user.major"})
    Page<Post> findAll(Specification spec, Pageable pageable);
}