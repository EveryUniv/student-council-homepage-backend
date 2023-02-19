package com.dku.council.global.interceptor;

import com.dku.council.global.dto.SuccessResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Component
public class VoidSuccessResponseInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception ex) throws Exception {
        final ContentCachingResponseWrapper cachingResponse = (ContentCachingResponseWrapper) response;

        HttpStatus status = HttpStatus.valueOf(response.getStatus());
        if (!status.is2xxSuccessful()) {
            return;
        }

        if (cachingResponse.getContentType() != null) {
            return;
        }

        String wrappedBody = objectMapper.writeValueAsString(new SuccessResponseDto());

        byte[] bytesData = wrappedBody.getBytes();
        cachingResponse.setContentType("application/json");
        cachingResponse.resetBuffer();
        cachingResponse.getOutputStream().write(bytesData, 0, bytesData.length);
    }
}
