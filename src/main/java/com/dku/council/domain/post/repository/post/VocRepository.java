package com.dku.council.domain.post.repository.post;

import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.posttype.Voc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VocRepository extends GenericPostRepository<Voc> , JpaSpecificationExecutor<Voc> {

    @Override
    @EntityGraph(attributePaths = {"user", "user.major"})
    Page<Post> findAll(Specification spec, Pageable pageable);
}