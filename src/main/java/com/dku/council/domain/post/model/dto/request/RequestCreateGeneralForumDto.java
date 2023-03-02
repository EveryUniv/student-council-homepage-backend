package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.tag.model.entity.Tag;
import com.dku.council.domain.user.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class RequestCreateGeneralForumDto extends RequestCreateGenericPostDto<GeneralForum> {

    @NotNull
    @Schema(description = "태그 ID", example = "11")
    private final Long tagId;

    public RequestCreateGeneralForumDto(@NotBlank String title, @NotBlank String body, Long tagId, List<MultipartFile> files) {
        super(title, body, tagId, files);
        this.tagId = tagId;
    }

    public GeneralForum toEntity(User user, Tag tag) {
        return GeneralForum.builder()
                .body(getBody())
                .tag(tag)
                .title(getTitle())
                .user(user)
                .build();
    }
}
