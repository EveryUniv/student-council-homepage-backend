package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.tag.model.entity.Tag;
import com.dku.council.domain.user.model.entity.User;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
public class RequestCreateNewsDto extends RequestCreateGenericPostDto<News> {

    public RequestCreateNewsDto(@NotBlank String title, @NotBlank String body, Long categoryId, List<MultipartFile> files) {
        super(title, body, categoryId, files);
    }

    public News toEntity(User user, Tag tag) {
        return News.builder()
                .body(getBody())
                .category(tag)
                .title(getTitle())
                .user(user)
                .build();
    }
}
