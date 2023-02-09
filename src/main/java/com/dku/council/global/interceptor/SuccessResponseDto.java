package com.dku.council.global.interceptor;

import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class SuccessResponseDto<T> {
    private final Boolean success = true;
    private final T data;

    public SuccessResponseDto(@Nullable T data){
        this.data = data;
    }
}
