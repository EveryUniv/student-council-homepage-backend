package com.dku.council.domain.user.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class RequestLoginDto {

    @NotBlank
    @Schema(description = "아이디(학번)", example = "12345678")
    private final String studentId;

    @NotBlank
    @Schema(description = "비밀번호", example = "121212")
    private final String password;
}
