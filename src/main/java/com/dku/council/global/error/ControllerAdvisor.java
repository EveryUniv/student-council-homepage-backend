package com.dku.council.global.error;

import com.dku.council.global.error.exception.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
    protected ResponseEntity<ErrorResponseDto> badParameter(HttpMessageNotReadableException e, Locale locale) {
        return localizedException(new BadRequestException(e), locale);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> badParameter(BindException e, Locale locale) {
        return localizedException(new BindingFailedException(e), locale);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> badParameter(MethodArgumentTypeMismatchException e, Locale locale) {
        return localizedException(new BadRequestException(e), locale);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> badParameter(MissingServletRequestParameterException e, Locale locale) {
        return localizedException(new MissingRequiredParamterException(e), locale);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> badMethod(HttpRequestMethodNotSupportedException e, Locale locale) {
        return localizedException(new NotSupportedMethodException(e), locale);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> accessDenied(AccessDeniedException e, Locale locale) {
        return localizedException(new NotGrantedException(e), locale);
    }

    @ExceptionHandler
    protected ResponseEntity<ErrorResponseDto> unexpectedException(Exception e, Locale locale) {
        ErrorResponseDto dto = new ErrorResponseDto(messageSource, locale, LocalizedMessageException.of(e));
        log.error("Unexpected exception has occurred in controller advice: [id={}]", dto.getTrackingId(), e);
        return ResponseEntity.internalServerError().body(dto);
    }
}
