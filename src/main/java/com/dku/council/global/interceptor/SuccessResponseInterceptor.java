package com.dku.council.global.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class SuccessResponseInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        final ContentCachingResponseWrapper cachingResponse = (ContentCachingResponseWrapper) response;

        if(!String.valueOf(response.getStatus()).startsWith("2")) return;

        if(cachingResponse.getContentType() != null && cachingResponse.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)){
            if(cachingResponse.getContentAsByteArray().length != 0){
                String jsonBody = StreamUtils.copyToString(cachingResponse.getContentInputStream(), StandardCharsets.UTF_8);
                Object data = objectMapper.readValue(jsonBody, Object.class);
                SuccessResponseDto<Object> responseDto = new SuccessResponseDto<>(data);

                String body = objectMapper.writeValueAsString(responseDto);

                cachingResponse.resetBuffer();

                cachingResponse.getWriter().write(body);

            }
        }

    }
}
