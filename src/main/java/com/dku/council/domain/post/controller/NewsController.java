package com.dku.council.domain.post.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post/news")
public class NewsController {

    @GetMapping
    public void list() {

    }

    @PostMapping
    public void post() {

    }

    @GetMapping("/{id}")
    public void findOne(@PathVariable int id) {

    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {

    }
}
