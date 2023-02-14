package com.dku.council.global.error;

import com.dku.council.global.error.exception.BadRequestException;
import com.dku.council.global.error.exception.BindingFailedException;
import com.dku.council.global.error.exception.LocalizedMessageException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ControllerAdvisor {

    private final MessageSource messageSource;

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> localizedException(LocalizedMessageException e, Locale locale) {
        ErrorResponseDto dto = new ErrorResponseDto(messageSource, locale, e);
        log.error("A problem has occurred in controller advice: [id={}]", dto.getTrackingId(), e);
        return ResponseEntity.status(e.getStatus()).body(dto);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> badRequest(HttpMessageNotReadableException e, Locale locale) {
        return localizedException(new BadRequestException(e), locale);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> badRequest(BindException e, Locale locale) {
        return localizedException(new BindingFailedException(e), locale);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> exception(Exception e) {
        ErrorResponseDto dto = new ErrorResponseDto(e);
        log.error("Unexpected exception has occurred in controller advice: [id={}]", dto.getTrackingId(), e);
        return ResponseEntity.internalServerError().body(dto);
    }
}