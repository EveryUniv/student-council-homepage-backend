package com.dku.council.domain.user.repository;

import com.dku.council.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
