package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.tag.model.entity.Tag;
import com.dku.council.domain.user.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class RequestCreatePetitionDto extends RequestCreateGenericPostDto<Petition> {

    @NotNull
    @Schema(description = "태그 ID", example = "11")
    private final Long tagId;

    public RequestCreatePetitionDto(String title, String body, Long tagId, List<MultipartFile> files) {
        super(title, body, tagId, files);
        this.tagId = tagId;
    }

    public Petition toEntity(User user, Tag tag) {
        return Petition.builder()
                .body(getBody())
                .title(getTitle())
                .tag(tag)
                .user(user)
                .petitionStatus(PetitionStatus.ACTIVE)
                .build();
    }
}
