package com.dku.council.domain.user;

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
class DepartmentTest {

    @Mock
    MessageSource messageSource;

    @Test
    @DisplayName("Department name이 국제화가 잘 되는지?")
    void getName() {
        // given
        when(messageSource.getMessage(Mockito.startsWith("Department."), any(), any())).thenReturn("SW융합대학");

        // when
        String majorName = Major.COMPUTER_SCIENCE.getDepartment().getName(messageSource);

        // then
        assertThat(majorName).isEqualTo("SW융합대학");
    }
}