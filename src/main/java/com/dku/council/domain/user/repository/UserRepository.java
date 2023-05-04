package com.dku.council.domain.user.repository;

import com.dku.council.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("select u from User u " +
            "join fetch u.major " +
            "where u.id = :id")
    Optional<User> findByIdWithMajor(@Param("id") Long id);

    @EntityGraph(attributePaths = {"major"})
    Optional<User> findByStudentId(String studentId);

    Optional<User> findByPhone(String phone);

    Optional<User> findByNickname(String nickname);
}
