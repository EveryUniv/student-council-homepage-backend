package com.dku.council.global.auth.role;

import com.dku.council.global.auth.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SecurityRequirement(name = JwtProvider.AUTHORIZATION)
public @interface GuestAuth {
}
