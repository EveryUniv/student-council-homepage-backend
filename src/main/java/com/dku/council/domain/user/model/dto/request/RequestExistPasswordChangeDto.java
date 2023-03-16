package com.dku.council.domain.user.model.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestExistPasswordChangeDto {
    @NotBlank
    @Size(min = 3, max = 200)
    private final String password;

    @NotBlank
    @Size(min = 3, max = 200)
    private final String newPassword;
}
