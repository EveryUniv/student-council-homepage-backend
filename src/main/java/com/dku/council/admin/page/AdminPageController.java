package com.dku.council.admin.page;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manage")
@RequiredArgsConstructor
public class AdminPageController {
    @GetMapping
    public String admin(){
        return "admin/index";
    }
}
