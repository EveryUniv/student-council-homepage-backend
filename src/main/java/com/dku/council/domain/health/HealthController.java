package com.dku.council.domain.health;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health Check", description = "서버 health 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping
public class HealthController {

    /**
     * health를 체크
     */
    @GetMapping
    public void healthCheck(){
    }
}
