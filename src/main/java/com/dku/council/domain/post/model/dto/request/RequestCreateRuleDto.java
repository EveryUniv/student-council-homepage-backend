package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.post.model.entity.posttype.Rule;
import com.dku.council.domain.user.model.entity.User;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
public class RequestCreateRuleDto extends RequestCreatePostDto{
    public RequestCreateRuleDto(@NotBlank String title, @NotBlank String body, List<MultipartFile> files) {
        super(title, body, files);
    }

    public Rule toEntity(User user) {
        return Rule.builder()
                .body(getBody())
                .title(getTitle())
                .user(user)
                .build();
    }
}
