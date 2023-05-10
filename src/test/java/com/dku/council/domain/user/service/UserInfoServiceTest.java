package com.dku.council.domain.user.service;

import com.dku.council.domain.comment.repository.CommentRepository;
import com.dku.council.domain.like.service.LikeService;
import com.dku.council.domain.post.repository.post.PostRepository;
import com.dku.council.domain.user.model.UserInfo;
import com.dku.council.domain.user.model.dto.response.ResponseUserInfoDto;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserInfoMemoryRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.UserMock;
import com.dku.council.util.ClockUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.Optional;

import static com.dku.council.domain.like.model.LikeTarget.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    private final Clock clock = ClockUtil.create();

    @Mock
    private UserRepository persistenceRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeService likeService;

    @Mock
    private UserInfoMemoryRepository memoryRepository;

    private UserInfoService service;

    @BeforeEach
    public void setup() {
        this.service = new UserInfoService(clock, persistenceRepository, memoryRepository, commentRepository, likeService, postRepository);
    }


    @Test
    @DisplayName("Full 내 정보 가져오기")
    void getFullUserInfo() {
        // given
        Major major = MajorMock.create();
        User user = UserMock.create(major);

        when(persistenceRepository.findByIdWithMajor(user.getId())).thenReturn(Optional.of(user));
        when(postRepository.countAllByUserId(user.getId())).thenReturn(1L);
        when(commentRepository.countAllCommentedByUserId(user.getId())).thenReturn(2L);
        when(likeService.getCountOfLikedElements(user.getId(), POST)).thenReturn(3L);

        // when
        ResponseUserInfoDto info = service.getFullUserInfo(user.getId());

        // then
        assertThat(info.getStudentId()).isEqualTo(user.getStudentId());
        assertThat(info.getUsername()).isEqualTo(user.getName());
        assertThat(info.getNickname()).isEqualTo(user.getNickname());
        assertThat(info.getYearOfAdmission()).isEqualTo(user.getYearOfAdmission().toString());
        assertThat(info.getMajor()).isEqualTo(user.getMajor().getName());
        assertThat(info.getDepartment()).isEqualTo(user.getMajor().getDepartment());
        assertThat(info.isAdmin()).isEqualTo(user.getUserRole().isAdmin());
        assertThat(info.getPhoneNumber()).isEqualTo(user.getPhone());
        assertThat(info.getWritePostCount()).isEqualTo(1);
        assertThat(info.getCommentedPostCount()).isEqualTo(2);
        assertThat(info.getLikedPostCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Full 내 정보 가져오기 실패 - 찾을 수 없는 아이디")
    void failedGetUserInfoByNotFound() {
        // given
        when(persistenceRepository.findByIdWithMajor(0L)).thenReturn(Optional.empty());

        // when
        assertThrows(UserNotFoundException.class, () ->
                service.getFullUserInfo(0L));
    }

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