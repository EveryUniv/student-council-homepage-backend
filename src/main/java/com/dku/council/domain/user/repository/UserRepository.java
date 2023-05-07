package com.dku.council.domain.user.repository;

import com.dku.council.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("select u from User u where u.status = 'ACTIVE' and u.id = :id ")
    Optional<User> findById(@Param("id") Long id);

    @Query("select u from User u " +
            "join fetch u.major " +
            "where u.id = :id and u.status = 'ACTIVE' ")
    Optional<User> findByIdWithMajor(@Param("id") Long id);

    @EntityGraph(attributePaths = {"major"})
    @Query("select u from User u where u.status = 'ACTIVE' and u.studentId = :studentId ")
    Optional<User> findByStudentId(@Param("studentId") String studentId);

    @Query("select u from User u where u.status = 'ACTIVE' and u.phone = :phone")
    Optional<User> findByPhone(@Param("phone") String phone);

    @Query("select u from User u where u.status = 'ACTIVE' and u.nickname = :nickname ")
    Optional<User> findByNickname(@Param("nickname") String nickname);

    @Query("select u from User u where u.status = 'INACTIVE' and u.studentId = :studentId ")
    Optional<User> findByStudentIdWithInactive(@Param("studentId") String studentId);
}
