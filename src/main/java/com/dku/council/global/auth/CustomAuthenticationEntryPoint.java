package com.dku.council.global.auth;

import com.dku.council.global.exception.CustomException;
import com.dku.council.global.exception.ErrorCode;
import com.dku.council.global.exception.ExceptionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        CustomException customException = new CustomException(ErrorCode.ACCESS_TOKEN_REQUIRED);

        JwtAuthenticationFilter.makeExceptionResponse(response, customException);
    }
}
