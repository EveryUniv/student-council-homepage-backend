package com.dku.council.domain.user.service;

import com.dku.council.domain.user.model.UserInfo;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserInfoMemoryRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    @Mock
    private UserRepository persistenceRepository;

    @Mock
    private UserInfoMemoryRepository memoryRepository;

    @InjectMocks
    private UserInfoService service;


    @Test
    @DisplayName("GetUserInfo - 캐싱되어있지 않은 경우")
    void getUserInfo() {
        // given
        User user = UserMock.createDummyMajor();
        UserInfo info = new UserInfo(user);

        when(memoryRepository.getUserInfo(eq(1L), any()))
                .thenReturn(Optional.empty());
        when(persistenceRepository.findByIdWithMajor(eq(1L)))
                .thenReturn(Optional.of(user));

        // when
        UserInfo result = service.getUserInfo(1L);

        // then
        assertThat(result).isEqualTo(info);
        verify(memoryRepository).setUserInfo(eq(1L), eq(info), any());
    }

    @Test
    @DisplayName("GetUserInfo - 캐싱된 경우")
    void getUserInfoCached() {
        // given
        User user = UserMock.createDummyMajor();
        UserInfo info = new UserInfo(user);

        when(memoryRepository.getUserInfo(eq(1L), any()))
                .thenReturn(Optional.of(info));

        // when
        UserInfo result = service.getUserInfo(1L);

        // then
        assertThat(result).isEqualTo(info);
    }

    @Test
    @DisplayName("캐싱된 정보 삭제")
    void invalidateUserInfo() {
        // when
        service.invalidateUserInfo(1L);

        // then
        verify(memoryRepository).removeUserInfo(1L);
    }

    @Test
    @DisplayName("직접 캐싱")
    void cacheUserInfo() {
        // given
        User user = UserMock.createDummyMajor();
        UserInfo info = new UserInfo(user);

        // when
        service.cacheUserInfo(1L, user);

        // then
        verify(memoryRepository).setUserInfo(eq(1L), eq(info), any());
    }
}