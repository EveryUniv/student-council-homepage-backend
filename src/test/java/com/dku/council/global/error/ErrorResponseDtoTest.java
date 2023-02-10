package com.dku.council.global.error;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorResponseDtoTest {

    @Mock
    private MessageSource messageSource;

    @Test
    @DisplayName("LocalizedMessageException로부터 정확하게 잘 생성되는가?")
    public void create_properly_localized() {
        // given
        LocalizedMessageException e = new LocalizedMessageException(HttpStatus.OK, "messageId");
        when(messageSource.getMessage(any(), any(), any(), any())).thenReturn("localizedMessage");

        // when
        ErrorResponseDto dto = new ErrorResponseDto(messageSource, Locale.KOREA, e);

        // then
        DateTimeFormatter.ISO_DATE_TIME.parse(dto.getTimestamp());
        assertThat(dto.getCode()).isEqualTo("LocalizedMessageException");
        assertThat(dto.getMessage()).isEqualTo("localizedMessage");
        assertThat(dto.getStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Exception로부터 정확하게 잘 생성되는가?")
    public void create_properly_exception() {
        // given
        Exception e = new RuntimeException("message");

        // when
        ErrorResponseDto dto = new ErrorResponseDto(e);

        // then
        DateTimeFormatter.ISO_DATE_TIME.parse(dto.getTimestamp());
        assertThat(dto.getCode()).isEqualTo("RuntimeException");
        assertThat(dto.getMessage()).isEqualTo("message");
        assertThat(dto.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}