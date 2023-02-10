package com.dku.council.global.error;

import com.dku.council.global.error.exception.LocalizedMessageException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MessageSource messageSource;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (LocalizedMessageException ex) {
            log.error("runtime exception exception handler filter", ex);
            makeExceptionResponse(response, request.getLocale(), ex);
        }
    }

    private void makeExceptionResponse(HttpServletResponse response, Locale locale, LocalizedMessageException e) throws IOException {
        response.setStatus(e.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponseDto exceptionDto = new ErrorResponseDto(messageSource, locale, e);
        String exceptionMessage = objectMapper.writeValueAsString(exceptionDto);
        response.getWriter().write(exceptionMessage);
    }
}
