package com.dku.council.domain.like.service;

import com.dku.council.domain.like.model.LikeEntry;
import com.dku.council.domain.like.model.LikeState;
import com.dku.council.domain.like.model.entity.LikeElement;
import com.dku.council.domain.like.repository.LikeMemoryRepository;
import com.dku.council.domain.like.repository.LikePersistenceRepository;
import com.dku.council.domain.like.service.impl.CachedLikeServiceImpl;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.dku.council.domain.like.model.LikeTarget.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachedLikeServiceImplTest {

    @Mock
    private LikeMemoryRepository memoryRepository;

    @Mock
    private LikePersistenceRepository persistenceRepository;

    @Mock
    private UserRepository userRepository;

    private CachedLikeServiceImpl service;

    private final Duration cacheTime = Duration.ofHours(1);


    @BeforeEach
    public void beforeEach() {
        service = new CachedLikeServiceImpl(memoryRepository, userRepository, persistenceRepository, cacheTime);
    }

    @Test
    @DisplayName("좋아요")
    void likeNoCached() {
        // when
        service.like(10L, 10L, POST);

        // then
        verify(memoryRepository).like(10L, 10L, POST);
        verify(memoryRepository).increaseLikeCount(10L, POST);
    }

    @Test
    @DisplayName("좋아요 - 이미 좋아요한 경우 무시")
    void likeAlready() {
        // given
        when(memoryRepository.isLiked(10L, 10L, POST)).thenReturn(true);

        // when
        service.like(10L, 10L, POST);

        // then
        verify(memoryRepository, never()).like(10L, 10L, POST);
        verify(memoryRepository, never()).setLikeCount(any(), eq(5), eq(POST), eq(cacheTime));
    }

    @Test
    @DisplayName("좋아요 취소")
    void cancelLikeNoCached() {
        // given
        when(memoryRepository.isLiked(10L, 10L, POST)).thenReturn(true);

        // when
        service.cancelLike(10L, 10L, POST);

        // then
        verify(memoryRepository).cancelLike(10L, 10L, POST);
        verify(memoryRepository).decreaseLikeCount(10L, POST);
    }

    @Test
    @DisplayName("좋아요 - 좋아요를 안한경우 무시")
    void cancelLikeAlready() {
        // given
        when(memoryRepository.isLiked(10L, 10L, POST)).thenReturn(false);

        // when
        service.cancelLike(10L, 10L, POST);

        // then
        verify(memoryRepository, never()).cancelLike(10L, 10L, POST);
        verify(memoryRepository, never()).setLikeCount(any(), eq(5), eq(POST), eq(cacheTime));
    }

    @Test
    @DisplayName("좋아요 확인 - 캐시에 좋아요가 등록된 경우")
    void isPostLikedCached() {
        // given
        when(memoryRepository.isLiked(10L, 10L, POST)).thenReturn(true);

        // when
        boolean liked = service.isLiked(10L, 10L, POST);

        // then
        assertThat(liked).isEqualTo(true);
    }

    @Test
    @DisplayName("좋아요 확인 - 캐시에 좋아요가 등록안되었고 DB에도 없는 경우")
    void isPostLikedNoCachedNoDB() {
        // given
        Optional<LikeElement> result = Optional.empty();
        when(memoryRepository.isLiked(10L, 10L, POST)).thenReturn(null);
        when(persistenceRepository.findByElementIdAndUserId(10L, 10L, POST)).thenReturn(result);

        // when
        boolean liked = service.isLiked(10L, 10L, POST);

        // then
        assertThat(liked).isEqualTo(false);
        verify(memoryRepository).setIsLiked(10L, 10L, POST, false);
    }

    @Test
    @DisplayName("좋아요 확인 - 캐시에 좋아요가 등록안되었고 DB에는 있는 경우")
    void isPostLikedNoCached() {
        // given
        Optional<LikeElement> result = Optional.of(new LikeElement(UserMock.createDummyMajor(), 5L, POST));
        when(memoryRepository.isLiked(5L, 10L, POST)).thenReturn(null);
        when(persistenceRepository.findByElementIdAndUserId(5L, 10L, POST)).thenReturn(result);

        // when
        boolean liked = service.isLiked(5L, 10L, POST);

        // then
        assertThat(liked).isEqualTo(true);
        verify(memoryRepository).setIsLiked(5L, 10L, POST, true);
    }

    @Test
    @DisplayName("좋아요 개수 확인 - 캐싱된 경우")
    void getCountOfLikesCached() {
        // given
        when(memoryRepository.getCachedLikeCount(10L, POST)).thenReturn(10);

        // when
        int likes = service.getCountOfLikes(10L, POST);

        // then
        assertThat(likes).isEqualTo(10);
    }

    @Test
    @DisplayName("좋아요 개수 확인 - 캐싱안된 경우")
    void getCountOfLikesNoCached() {
        // given
        when(memoryRepository.getCachedLikeCount(10L, POST)).thenReturn(-1);
        when(persistenceRepository.countByElementIdAndTarget(10L, POST)).thenReturn(10);

        // when
        int likes = service.getCountOfLikes(10L, POST);

        // then
        assertThat(likes).isEqualTo(10);
    }

    @Test
    @DisplayName("좋아요 누른 요소들 조회")
    void getLikedElementIds() {
        // given
        List<LikeEntry> likeEntries = makeLikeEntryList();
        List<LikeElement> likes = LongStream.range(0, 10)
                .mapToObj(i -> new LikeElement(null, i, POST))
                .collect(Collectors.toList());

        when(persistenceRepository.findAllByUserId(6L, POST, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(likes));
        when(memoryRepository.getAllLikesAndClear(6L, POST)).thenReturn(likeEntries);
        when(userRepository.getReferenceById(any()))
                .thenAnswer(inv -> UserMock.createDummyMajor(inv.getArgument(0)));

        // when
        Page<Long> actual = service.getLikedElementIds(6L, Pageable.unpaged(), POST);

        // then
        assertThat(actual.getTotalElements()).isEqualTo(10);
        verify(persistenceRepository, times(10)).save(any(LikeElement.class));

        for (int i = 10; i < 20; i++) {
            Long l = (long) i;
            verify(persistenceRepository).deleteByElementIdAndUserId(l, 6L, POST);
        }
    }

    @Test
    @DisplayName("좋아요 누른 요소 개수 조회")
    void getCountOfLikedElements() {
        // given
        List<LikeEntry> likeEntries = makeLikeEntryList();

        when(persistenceRepository.countPostByUserId(6L, POST)).thenReturn(10L);
        when(memoryRepository.getAllLikesAndClear(6L, POST)).thenReturn(likeEntries);
        when(userRepository.getReferenceById(any()))
                .thenAnswer(inv -> UserMock.createDummyMajor(inv.getArgument(0)));

        // when
        Long size = service.getCountOfLikedElements(6L, POST);

        // then
        assertThat(size).isEqualTo(10);
        verify(persistenceRepository, times(10)).save(any(LikeElement.class));

        for (int i = 10; i < 20; i++) {
            Long l = (long) i;
            verify(persistenceRepository).deleteByElementIdAndUserId(l, 6L, POST);
        }
    }

    @Test
    @DisplayName("Memory에 캐시된 좋아요를 DB로 dump")
    void dumpToDB() {
        // given
        List<LikeEntry> likes = makeLikeEntryList();

        when(memoryRepository.getAllLikesAndClear(POST)).thenReturn(Map.of(6L, likes, 7L, likes));
        when(userRepository.getReferenceById(any()))
                .thenAnswer(inv -> UserMock.createDummyMajor(inv.getArgument(0)));

        // when
        service.dumpToDB(POST);

        // then
        verify(persistenceRepository, times(20)).save(any(LikeElement.class));

        for (int i = 10; i < 20; i++) {
            Long l = (long) i;
            verify(persistenceRepository).deleteByElementIdAndUserId(l, 6L, POST);
        }

        for (int i = 10; i < 20; i++) {
            Long l = (long) i;
            verify(persistenceRepository).deleteByElementIdAndUserId(l, 7L, POST);
        }
    }

    private static List<LikeEntry> makeLikeEntryList() {
        Stream<LikeEntry> likeStream = LongStream.range(0, 10)
                .mapToObj(i -> new LikeEntry(i, LikeState.LIKED));
        Stream<LikeEntry> cancelledStream = LongStream.range(10, 20)
                .mapToObj(i -> new LikeEntry(i, LikeState.CANCELLED));
        return Stream.concat(likeStream, cancelledStream)
                .collect(Collectors.toList());
    }
}