package com.dku.council.domain.mainpage.service;

import com.dku.council.domain.mainpage.model.dto.request.RequestCarouselImageDto;
import com.dku.council.domain.mainpage.model.dto.response.CarouselImageResponse;
import com.dku.council.domain.mainpage.model.dto.response.MainPageResponseDto;

import java.util.List;

public interface MainPageService {
    /**
     * 캐러셀 이미지 목록을 가져옵니다.
     * @return
     */
    List<CarouselImageResponse> getCarouselImages();

    /**
     * 캐러셀 이미지를 등록합니다.
     * @param requestCarouselImageDto
     */
    void addCarouselImage(RequestCarouselImageDto requestCarouselImageDto);

    /**
     * 캐러셀 id 로 저장되어 있는 Object 를 삭제합니다.
     * @param carouselId
     */
    void deleteCarouselImage(Long carouselId);
    MainPageResponseDto getMainPage();
}
