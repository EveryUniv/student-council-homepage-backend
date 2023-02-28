package com.dku.council.domain.post.model.dto.request;

import com.dku.council.domain.category.Category;
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

    @NotNull(message = "회차를 등록해주세요")
    @Schema(description = "회차", example = "4")
    private final Integer round;

    @Pattern(regexp = "^\\d{4}-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$",
            message = "개최일자는 YYYY-MM-dd(YYYY-M-d) 형식만 가능합니다.")
    @Schema(description = "회의 날짜. (YYYY-MM-dd / YYYY-M-d)", example = "2023-03-01",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private final String date;

    public RequestCreateConferenceDto(String title, String body, Long categoryId, List<MultipartFile> files, Integer round, String date) {
        super(title, body, categoryId, files);
        this.round = round;
        this.date = date;
    }

    public Conference toEntity(User user, Category category) {
        return Conference.builder()
                .body(getBody())
                .title(getTitle())
                .category(category)
                .user(user)
                .round(getRound())
                .date(LocalDate.parse(getDate()))
                .build();
    }
}
