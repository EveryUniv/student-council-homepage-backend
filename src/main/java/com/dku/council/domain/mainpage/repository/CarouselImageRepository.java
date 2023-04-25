package com.dku.council.domain.mainpage.repository;

import com.dku.council.domain.mainpage.model.entity.CarouselImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarouselImageRepository extends JpaRepository<CarouselImage, Long> {
    List<CarouselImage> findAllByOrderByCreatedAtDesc();
}
