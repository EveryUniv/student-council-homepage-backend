package com.dku.council.domain.user.repository.impl;

import com.dku.council.domain.user.model.UserInfo;
import com.dku.council.mock.UserInfoMock;
import com.dku.council.util.base.AbstractContainerRedisTest;
import com.dku.council.util.test.FullIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@FullIntegrationTest
class UserInfoRedisRepositoryTest extends AbstractContainerRedisTest {

    @Autowired
    private UserInfoRedisRepository repository;

    private final Instant now = Instant.ofEpochSecond(123456789);


    @Test
    @DisplayName("유저 정보 저장 및 반환")
    void saveAndGetUserInfo() {
        // given
        UserInfo userInfo = UserInfoMock.create();
        repository.setUserInfo(5L, userInfo, now);

        // when
        Optional<UserInfo> info = repository.getUserInfo(5L, now);

        // then
        assertThat(info.orElseThrow()).isEqualTo(userInfo);
    }

    @Test
    @DisplayName("유저 정보가 없으면 null 반환")
    void getUserInfoEmpty() {
        // when
        Optional<UserInfo> info = repository.getUserInfo(5L, now);

        // then
        assertThat(info).isEmpty();
    }

    @Test
    @DisplayName("유저 정보 캐시 삭제")
    void removeUserInfo() {
        // given
        UserInfo userInfo = UserInfoMock.create();
        repository.setUserInfo(5L, userInfo, now);

        // when
        repository.removeUserInfo(5L);

        // then
        Optional<UserInfo> info = repository.getUserInfo(5L, now);
        assertThat(info).isEmpty();
    }
}