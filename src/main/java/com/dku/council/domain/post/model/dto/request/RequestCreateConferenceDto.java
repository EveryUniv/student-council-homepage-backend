package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.post.model.entity.posttype.Conference;
import com.dku.council.domain.user.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

@Getter
public class RequestCreateConferenceDto extends RequestCreateGenericPostDto<Conference> {

    @NotNull
    @Schema(description = "회차", example = "4")
    private final Integer round;

    @Pattern(regexp = "^\\d{4}-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$")
    @Schema(description = "회의 날짜. (YYYY-MM-dd / YYYY-M-d)", example = "2023-03-01",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private final String date;

    public RequestCreateConferenceDto(String title, String body, List<Long> tagIds, List<MultipartFile> files, Integer round, String date) {
        super(title, body, tagIds, files);
        this.round = round;
        this.date = date;
    }

    public Conference toEntity(User user) {
        return Conference.builder()
                .body(getBody())
                .title(getTitle())
                .user(user)
                .round(getRound())
                .date(LocalDate.parse(getDate()))
                .build();
    }
}
