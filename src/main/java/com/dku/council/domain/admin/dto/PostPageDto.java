package com.dku.council.domain.admin.dto;

import com.dku.council.domain.post.model.entity.Post;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PostPageDto {
    private final Long id;
    private final Long userId;
    private final String title;
    private final LocalDateTime createdAt;
    private final String status;
    private final int reportCount;

    public PostPageDto(Post post) {
        this.id = post.getId();
        this.userId = post.getUser().getId();
        this.title = post.getTitle();
        this.createdAt = post.getCreatedAt();
        this.status = post.getStatus().toString();
        this.reportCount = post.getReportedCount();
    }
}
