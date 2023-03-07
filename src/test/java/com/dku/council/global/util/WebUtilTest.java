package com.dku.council.global.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WebUtilTest {

    @Test
    @DisplayName("쿠키와 key-value block")
    void cookieAndKeyValueBlock() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.add("set-cookie", "name=value; path=/api");

        // when
        List<ResponseCookie> cookies = WebUtil.extractCookies(headers);

        // then
        ResponseCookie cookie = cookies.get(0);
        assertThat(cookie.getName()).isEqualTo("name");
        assertThat(cookie.getValue()).isEqualTo("value");
        assertThat(cookie.getPath()).isEqualTo("/api");
        assertThat(cookie.isSecure()).isEqualTo(false);
    }

    @Test
    @DisplayName("쿠키와 단일 값 block")
    void cookieAndPlainBlock() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.add("set-cookie", "name=value; SeCuRe");

        // when
        List<ResponseCookie> cookies = WebUtil.extractCookies(headers);

        // then
        ResponseCookie cookie = cookies.get(0);
        assertThat(cookie.getName()).isEqualTo("name");
        assertThat(cookie.getValue()).isEqualTo("value");
        assertThat(cookie.isSecure()).isEqualTo(true);
    }

    @Test
    @DisplayName("쿠키와 '-'가 들어간 key-value block")
    void cookieAndDashedKeyValueBlock() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.add("set-cookie", "name=value; max-age=1551");

        // when
        List<ResponseCookie> cookies = WebUtil.extractCookies(headers);

        // then
        ResponseCookie cookie = cookies.get(0);
        assertThat(cookie.getName()).isEqualTo("name");
        assertThat(cookie.getValue()).isEqualTo("value");
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(1551);
    }

    @Test
    @DisplayName("쿠키와 복합 블록")
    void cookieAndCompositeBlock() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.add("set-cookie", "name=value; PaTh=/api; max-age=1551;SECURE  ; domain=  www.com  ");

        // when
        List<ResponseCookie> cookies = WebUtil.extractCookies(headers);

        // then
        ResponseCookie cookie = cookies.get(0);
        assertThat(cookie.getName()).isEqualTo("name");
        assertThat(cookie.getValue()).isEqualTo("value");
        assertThat(cookie.getPath()).isEqualTo("/api");
        assertThat(cookie.getDomain()).isEqualTo("www.com");
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(1551);
        assertThat(cookie.isSecure()).isEqualTo(true);
    }

    @Test
    @DisplayName("쿠키와 모르는 블록")
    void cookieAndUnknownBlock() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.add("set-cookie", "name=value; blockname=value");

        // when
        List<ResponseCookie> cookies = WebUtil.extractCookies(headers);

        // then
        ResponseCookie cookie = cookies.get(0);
        assertThat(cookie.getName()).isEqualTo("name");
        assertThat(cookie.getValue()).isEqualTo("value");
    }
}