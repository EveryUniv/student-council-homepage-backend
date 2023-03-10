package com.dku.council.domain.page.service;

import com.dku.council.domain.page.model.dto.request.CarouselImageRequestDto;
import com.dku.council.domain.page.model.dto.response.CarouselImageResponse;
import com.dku.council.domain.page.model.dto.response.MainPageResponseDto;

import java.util.List;

public interface PageService {
    List<CarouselImageResponse> getCarouselImages();
    void addCarouselImage(CarouselImageRequestDto dto);

    void deleteCarouselImage(Long carouselId);
    MainPageResponseDto getMainPage();
}
