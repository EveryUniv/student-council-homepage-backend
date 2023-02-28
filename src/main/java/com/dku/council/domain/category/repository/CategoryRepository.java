package com.dku.council.domain.category.repository;

import com.dku.council.domain.category.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
