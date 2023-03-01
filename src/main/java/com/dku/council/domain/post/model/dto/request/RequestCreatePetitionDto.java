package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.category.model.entity.Category;
import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.user.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
public class RequestCreatePetitionDto extends RequestCreateGenericPostDto<Petition> {

    @NotBlank
    @Schema(description = "카테고리 ID", example = "11")
    private final Long categoryId;

    public RequestCreatePetitionDto(String title, String body, Long categoryId, List<MultipartFile> files) {
        super(title, body, categoryId, files);
        this.categoryId = categoryId;
    }

    public Petition toEntity(User user, Category category) {
        return Petition.builder()
                .body(getBody())
                .title(getTitle())
                .category(category)
                .user(user)
                .petitionStatus(PetitionStatus.ACTIVE)
                .build();
    }
}
