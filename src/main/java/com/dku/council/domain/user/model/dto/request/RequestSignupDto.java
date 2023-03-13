package com.dku.council.domain.user.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@RequiredArgsConstructor
public class RequestSignupDto {

    @NotBlank
    @Size(min = 3, max = 16)
    @Pattern(regexp = "[가-힣ㄱ-ㅎㅏ-ㅣA-Za-z\\d_ ]*")
    private final String nickname;

    @NotBlank
    @Size(min = 3, max = 200)
    private final String password;
}
