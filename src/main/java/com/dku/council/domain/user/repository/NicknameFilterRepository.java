package com.dku.council.domain.user.repository;

import com.dku.council.domain.user.model.entity.NicknameFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NicknameFilterRepository extends JpaRepository<NicknameFilter, Long> {
    @Query("select count(*) from NicknameFilter f " +
            "where upper(:nickname) like concat('%', upper(f.word), '%')")
    Long countMatchedFilter(@Param("nickname") String nickname);
}
