package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.user.model.entity.User;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class RequestCreatePetitionDto extends RequestCreateGenericPostDto<Petition> {

    public RequestCreatePetitionDto(String title, String body, List<Long> tagIds, List<MultipartFile> files) {
        super(title, body, tagIds, files);
    }

    public Petition toEntity(User user) {
        return Petition.builder()
                .body(getBody())
                .title(getTitle())
                .user(user)
                .build();
    }
}
