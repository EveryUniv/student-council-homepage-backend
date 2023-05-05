package com.dku.council.domain.post.repository.post;

import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface GeneralForumRepository extends GenericPostRepository<GeneralForum> {

    /**
     * UserID를 통해 ACTIVE상태인 post를 가져옵니다.
     */
    @Query("select p from GeneralForum p where p.user.id=:userId and p.status='ACTIVE'")
    Page<GeneralForum> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
}