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
            writeErrorResponse(response, response.getLocale(), ex);
        }
    }

    private void writeErrorResponse(HttpServletResponse response, Locale locale, LocalizedMessageException ex) throws IOException {
        response.setStatus(ex.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorResponseDto dto = new ErrorResponseDto(messageSource, locale, ex);
        log.error("A problem has occurred in filter: [id={}]", dto.getTrackingId(), ex);

        String exceptionMessage = objectMapper.writeValueAsString(dto);
        response.getWriter().write(exceptionMessage);
    }
}
