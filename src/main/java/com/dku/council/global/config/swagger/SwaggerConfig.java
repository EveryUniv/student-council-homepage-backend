package com.dku.council.global.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "단국대학교 총학생회 홈페이지 서버 API",
                version = SwaggerConfig.API_VERSION,
                description = "단국대학교 프론트엔드 개발에 활용할 수 있는 RESTFUL API 제공"
        ),
        servers = {
                @Server(url = "https://dev.dkustu.com", description = "개발 서버"),
                @Server(url = "/", description = "로컬 서버")
        }
)
@SecurityScheme(
        name = SwaggerConfig.AUTHENTICATION,
        type = SecuritySchemeType.HTTP,
        scheme = "Bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
    public static final String AUTHENTICATION = "Authorization";
    public static final String API_VERSION = "v1.0.0";
}
