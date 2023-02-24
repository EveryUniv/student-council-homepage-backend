package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.service.RuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "총학 회칙", description = "총학 회칙 관련 api")
@RestController
@RequestMapping("/post/rule")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;
}
