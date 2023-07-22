package com.dku.council.domain.admin.controller;

import com.dku.council.domain.admin.dto.HomeBusPageDto;
import com.dku.council.domain.admin.service.HomeBusPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manage/home-bus")
public class HomeBusPageController {

    private final HomeBusPageService homeBusPageService;


    @GetMapping
    public String homeBus(Model model) {
        List<HomeBusPageDto> homeBus = homeBusPageService.getAllHomeBus();
        model.addAttribute("homeBus", homeBus);
        return "page/home-bus/home-bus";
    }
}
