package com.dku.council.global.auth;

import com.dku.council.global.exception.CustomException;
import com.dku.council.global.exception.ErrorCode;
import com.dku.council.global.exception.ExceptionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        CustomException customException = new CustomException(ErrorCode.NOT_GRANTED);

        JwtAuthenticationFilter.makeExceptionResponse(response, customException);
    }
}
