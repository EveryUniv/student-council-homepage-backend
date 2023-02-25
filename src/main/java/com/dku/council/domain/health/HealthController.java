package com.dku.council.domain.health;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class HealthController {

    /**
     * health를 체크하기 위한 api
     */
    @GetMapping
    public void healthCheck(){
    }
}
