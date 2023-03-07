package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.post.model.entity.posttype.Voc;
import com.dku.council.domain.user.model.entity.User;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
public class RequestCreateVocDto extends RequestCreateGenericPostDto<Voc> {

    public RequestCreateVocDto(String title, String body, List<Long> tagIds, List<MultipartFile> files) {
        super(title, body, tagIds, files);
    }

    public Voc toEntity(User user) {
        return Voc.builder()
                .body(getBody())
                .title(getTitle())
                .user(user)
                .build();
    }
}
