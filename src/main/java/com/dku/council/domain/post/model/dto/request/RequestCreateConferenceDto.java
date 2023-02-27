package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.post.model.entity.posttype.Conference;
import com.dku.council.domain.user.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

@Getter
public class RequestCreateConferenceDto extends RequestCreateGenericPostDto<Conference> {

    @NotBlank(message = "회차를 등록해주세요")
    @Schema(description = "회차", example = "4")
    private int round;

    @Pattern(regexp = "^\\d{4}-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$",
            message = "개최일자는 YYYY-MM-dd(YYYY-M-d) 형식만 가능합니다.")
    @Schema(description = "회의 날짜", example = "2023-03-01")
    private String date;

    public RequestCreateConferenceDto(@NotBlank String title, @NotBlank String body, Long categoryId, List<MultipartFile> files) {
        super(title, body, categoryId, files);
    }

    public Conference toEntity(User user) {
        return Conference.builder()
                .body(getBody())
                .title(getTitle())
                .user(user)
                .round(getRound())
                .date(LocalDate.parse(date))
                .build();
    }
}
