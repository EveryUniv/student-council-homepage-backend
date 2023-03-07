package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.user.model.entity.User;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
public class RequestCreateGeneralForumDto extends RequestCreateGenericPostDto<GeneralForum> {

    public RequestCreateGeneralForumDto(@NotBlank String title, @NotBlank String body, List<Long> tagIds, List<MultipartFile> files) {
        super(title, body, tagIds, files);
    }

    public GeneralForum toEntity(User user) {
        return GeneralForum.builder()
                .body(getBody())
                .title(getTitle())
                .user(user)
                .build();
    }
}
