package com.dku.council.global.auth.role;

import com.dku.council.global.auth.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.*;

/**
 * Guest 이상의 권한을 가진 사용자만 접근 가능한 API에 사용.
 * 이 Annotation을 지정하면 로그인한 경우 Swagger에서 인증 정보를 자동으로 넘겨줍니다.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SecurityRequirement(name = JwtProvider.AUTHORIZATION)
public @interface GuestAuth {
}
