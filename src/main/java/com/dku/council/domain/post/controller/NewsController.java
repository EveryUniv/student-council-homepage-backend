package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.dto.request.RequestCreateNewsDto;
import com.dku.council.domain.post.dto.response.ResponseSingleNewsDto;
import com.dku.council.global.dto.SuccessResponseDto;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/post/news")
public class NewsController {

    @GetMapping
    public void list() {
        // TODO Implementation
    }

    @PostMapping
    public void create(@Valid @RequestBody RequestCreateNewsDto request) {
        // TODO Implementation
    }

    @GetMapping("/{id}")
    public ResponseSingleNewsDto findOne(@PathVariable int id) {
        // TODO Implementation
        return ResponseSingleNewsDto.fromEntity(null);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        // TODO Implementation
    }
}
