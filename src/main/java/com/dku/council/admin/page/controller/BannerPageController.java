package com.dku.council.admin.page.controller;

import com.dku.council.domain.mainpage.model.dto.request.RequestCarouselImageDto;
import com.dku.council.domain.mainpage.model.dto.response.CarouselImageResponse;
import com.dku.council.domain.mainpage.service.MainPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/manage/banner")
@RequiredArgsConstructor
public class BannerPageController {
    /**
     * Banner 는 MainPageService 를 의존합니다.
     */
    private final MainPageService service;

    @GetMapping
    public String banner(Model model, RequestCarouselImageDto dto){
        List<CarouselImageResponse> all = service.getCarouselImages();
        model.addAttribute("banners", all);
        model.addAttribute("object", dto);
        return "banner/banner";
    }

    @PostMapping("/{bannerId}/delete")
    public String deleteBanner(@PathVariable Long bannerId){
        service.deleteCarouselImage(bannerId);
        return "redirect:/manage/banner";
    }

    @PostMapping("/add")
    public String addBanner(RequestCarouselImageDto dto){
        service.addCarouselImage(dto);
        return "redirect:/manage/banner";
    }

}
