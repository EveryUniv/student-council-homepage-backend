package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.user.model.entity.User;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
public class RequestCreateNewsDto extends RequestCreateGenericPostDto<News> {

    public RequestCreateNewsDto(@NotBlank String title, @NotBlank String body, List<Long> tagIds, List<MultipartFile> files) {
        super(title, body, tagIds, files);
    }

    public News toEntity(User user) {
        return News.builder()
                .body(getBody())
                .title(getTitle())
                .user(user)
                .build();
    }
}
