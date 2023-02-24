package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.category.Category;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.user.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
public class RequestCreateGeneralForumDto extends RequestCreatePostDto{

    @Schema(description = "카테고리 id")
    private final Long categoryId;

    public RequestCreateGeneralForumDto(@NotBlank String title, @NotBlank String body, List<MultipartFile> files, Long categoryId) {
        super(title, body, files);
        this.categoryId = categoryId;
    }

    public GeneralForum toEntity(User user, Category category) {
        return GeneralForum.builder()
                .body(getBody())
                .title(getTitle())
                .user(user)
                .category(category)
                .build();
    }
}
