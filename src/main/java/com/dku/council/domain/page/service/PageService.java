package com.dku.council.domain.page.service;

import com.dku.council.domain.page.model.dto.CarouselImageRequestDto;
import com.dku.council.domain.page.model.dto.CarouselImageResponse;
import com.dku.council.domain.page.model.dto.MainPageResponseDto;
import com.dku.council.domain.page.model.dto.PostSummary;
import com.dku.council.infra.nhn.model.UploadedFile;

import java.util.ArrayList;
import java.util.List;

public interface PageService {
    List<CarouselImageResponse> getCarouselImages();
    void addCarouselImage(CarouselImageRequestDto dto);

    void deleteCarouselImage(Long carouselId);
    MainPageResponseDto getMainPage();
}
