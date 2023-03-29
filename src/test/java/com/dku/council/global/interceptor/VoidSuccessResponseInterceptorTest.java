package com.dku.council.global.interceptor;

import com.dku.council.global.dto.ResponseSuccessDto;
import com.dku.council.util.ObjectMapperGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoidSuccessResponseInterceptorTest {

    @Mock
    private ServletOutputStream outputStream;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ContentCachingResponseWrapper response;

    private final ObjectMapper objectMapper = ObjectMapperGenerator.create();
    private VoidSuccessResponseInterceptor interceptor;


    @BeforeEach
    public void beforeEach() {
        this.interceptor = new VoidSuccessResponseInterceptor(objectMapper);
    }

    @Test
    @DisplayName("비어있는 성공 응답이 잘 들어가는가?")
    public void voidSuccessResponse() throws Exception {
        // given
        when(response.getStatus()).thenReturn(200);
        when(response.getContentType()).thenReturn(null);
        when(response.getOutputStream()).thenReturn(outputStream);
        byte[] bytes = objectMapper.writeValueAsString(new ResponseSuccessDto()).getBytes();

        // when
        interceptor.afterCompletion(request, response, new Object(), null);

        // then
        verify(response, atLeastOnce()).getOutputStream();
        verify(outputStream).write(bytes, 0, bytes.length);
    }

    @Test
    @DisplayName("실패한 status or redirect 응답을 잘 걸러내는가?")
    public void failStatusResponse() throws Exception {
        int[] failCodes = {302, 300, 401, 404, 500};
        for (int code : failCodes) {
            // given
            when(response.getStatus()).thenReturn(code);

            // when
            interceptor.afterCompletion(request, response, new Object(), null);

            // then
            verify(response, never()).getOutputStream();
        }
    }

    @Test
    @DisplayName("dto가 포함된 응답은 무시하는가?")
    public void afterCompletion() throws Exception {
        // given
        when(response.getStatus()).thenReturn(200);
        when(response.getContentType()).thenReturn("application/json");

        // when
        interceptor.afterCompletion(request, response, new Object(), null);

        // then
        verify(response, never()).getOutputStream();
    }
}