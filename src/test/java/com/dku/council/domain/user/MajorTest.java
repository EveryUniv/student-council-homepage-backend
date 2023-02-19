package com.dku.council.domain.user;

import com.dku.council.domain.user.model.Major;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MajorTest {

    @Mock
    MessageSource messageSource;

    @Test
    @DisplayName("Major name이 국제화가 잘 되는지?")
    void getName() {
        // given
        when(messageSource.getMessage(Mockito.startsWith("Major."), any(), any())).thenReturn("컴퓨터공학과");

        // when
        String majorName = Major.COMPUTER_SCIENCE.getName(messageSource);

        // then
        assertThat(majorName).isEqualTo("컴퓨터공학과");
    }
}