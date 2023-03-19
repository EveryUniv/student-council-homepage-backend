package com.dku.council.domain.mainpage.model.dto;

import com.dku.council.domain.post.model.entity.Post;
import lombok.Getter;

@Getter
public class PostSummary {
    private final Long id;
    private final String title;

    public PostSummary(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
    }
}
