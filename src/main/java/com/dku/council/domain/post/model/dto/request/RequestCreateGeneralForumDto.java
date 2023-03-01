package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.category.model.entity.Category;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
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
    @Schema(description = "카테고리 ID", example = "11")
    private final Long categoryId;

    public RequestCreateGeneralForumDto(@NotBlank String title, @NotBlank String body, Long categoryId, List<MultipartFile> files) {
        super(title, body, categoryId, files);
        this.categoryId = categoryId;
    }

    public GeneralForum toEntity(User user, Category category) {
        return GeneralForum.builder()
                .body(getBody())
                .category(category)
                .title(getTitle())
                .user(user)
                .build();
    }
}
