package com.dku.council.domain.user.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CodeGeneratorTest {

    @Test
    @DisplayName("코드가 숫자에 정확한 자리수로 잘 생성되는지")
    void generateDigitCode() {
        // given
        int count = 10;

        // when
        String code = CodeGenerator.generateDigitCode(count);

        // then
        assertThat(code.length()).isEqualTo(count);
        for (char c : code.toCharArray()) {
            assertThat(Character.isDigit(c)).isTrue();
        }
    }

    @Test
    @DisplayName("Email 코드가 정확한 자릿수로 잘 생성되는지")
    void generateEmailCode() {
        // given
        int count = 10;

        // when
        String code = CodeGenerator.generateHexCode(count);

        // then
        assertThat(code.length()).isEqualTo(count);
    }
}