package com.dku.council.domain.page.controller;

import com.dku.council.domain.page.model.dto.request.CarouselImageRequestDto;
import com.dku.council.domain.page.model.dto.response.CarouselImageResponse;
import com.dku.council.domain.page.model.dto.response.MainPageResponseDto;
import com.dku.council.domain.page.service.PageService;
import com.dku.council.global.auth.role.AdminOnly;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "메인 페이지", description = "메인 페이지 관련 api")
@RestController
@RequestMapping("/page")
@RequiredArgsConstructor
public class PageController {
    private final PageService pageService;

    @GetMapping("/main")
    public MainPageResponseDto index(){
        return pageService.getMainPage();
    }

    @PostMapping("/carousel")
    @AdminOnly
    public void uploadCarouselImage(@Valid @ModelAttribute CarouselImageRequestDto dto) {
        pageService.addCarouselImage(dto);
    }

    @GetMapping("/carousel")
    public List<CarouselImageResponse> getCarouselImages(){
        return pageService.getCarouselImages();
    }

    @DeleteMapping("/carousel/{id}")
    @AdminOnly
    public void deleteCarouselImage(@PathVariable("id") Long carouselId){
        pageService.deleteCarouselImage(carouselId);
    }
}
