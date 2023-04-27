package com.dku.council.domain.admin.controller;

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

import javax.servlet.http.HttpServletRequest;
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
    public String banner(Model model, RequestCarouselImageDto dto) {
        List<CarouselImageResponse> all = service.getCarouselImages();
        model.addAttribute("banners", all);
        model.addAttribute("object", dto);
        return "page/banner/banner";
    }

    @PostMapping("/{bannerId}/delete")
    public String deleteBanner(HttpServletRequest request, @PathVariable Long bannerId) {
        service.deleteCarouselImage(bannerId);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/{bannerId}/edit")
    public String editBanner(HttpServletRequest request, @PathVariable Long bannerId, String redirectUrl) {
        service.changeRedirectUrl(bannerId, redirectUrl);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/add")
    public String addBanner(HttpServletRequest request, RequestCarouselImageDto dto) {
        service.addCarouselImage(dto);
        return "redirect:" + request.getHeader("Referer");
    }

}
