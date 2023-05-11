package com.dku.council.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class WebConfigTest {

    @Test
    @DisplayName("CORS List가 잘 parse 되는지?")
    public void parseCorsList() throws Exception {
        // given
        Method method = WebConfig.class.getDeclaredMethod("parseCorsList", String.class);
        method.setAccessible(true);

        // when
        String[] result1 = (String[]) method.invoke(null, "*");
        String[] result2 = (String[]) method.invoke(null, "   \t   *       \n     ");
        String[] result3 = (String[]) method.invoke(null, "  http://localhost:3000 ,  http://localhost:8080    ");
        String[] result4 = (String[]) method.invoke(null, " http://localhost:3000     ,https://dku stu.com,https://dkustu.com/");
        String[] result5 = (String[]) method.invoke(null, " http://localhost:3000, http://localhost :8080,     *    ");

        // then
        assertThat(result1).containsExactly("*");
        assertThat(result2).containsExactly("*");
        assertThat(result3).containsExactly("http://localhost:3000", "http://localhost:8080");
        assertThat(result4).containsExactly("http://localhost:3000", "https://dku stu.com", "https://dkustu.com/");
        assertThat(result5).containsExactly("http://localhost:3000", "http://localhost :8080", "*");
    }
}