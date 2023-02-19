package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.dto.request.RequestCreateNewsDto;
import com.dku.council.global.dto.SuccessResponseDto;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/post/news")
public class NewsController {

    @GetMapping
    public void list() {

    }

    @PostMapping
    public void create(@Valid @RequestBody RequestCreateNewsDto request) {

    }

    @GetMapping("/{id}")
    public void findOne(@PathVariable int id) {

    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {

    }
}
