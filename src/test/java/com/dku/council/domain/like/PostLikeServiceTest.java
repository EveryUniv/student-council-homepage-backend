package com.dku.council.domain.like;

import com.dku.council.domain.like.model.LikeEntry;
import com.dku.council.domain.like.model.LikeState;
import com.dku.council.domain.like.repository.PostLikeMemoryRepository;
import com.dku.council.domain.like.repository.PostLikePersistenceRepository;
import com.dku.council.domain.post.repository.PostRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.NewsMock;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {

    @Mock
    private PostLikeMemoryRepository memoryRepository;

    @Mock
    private PostLikePersistenceRepository persistenceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostLikeService service;

    @Test
    @DisplayName("좋아요 - 캐시에 count가 없는 경우")
    void likeNoCached() {
        // given
        when(memoryRepository.getCachedLikeCount(any())).thenReturn(-1);
        when(persistenceRepository.countByPostId(any())).thenReturn(5);

        // when
        service.like(10L, 10L);

        // then
        verify(memoryRepository).addPostLike(10L, 10L);
        verify(memoryRepository).setLikeCount(any(), eq(6));
    }

    @Test
    @DisplayName("좋아요 - 캐시에 count가 있는 경우")
    void likeCached() {
        // given
        when(memoryRepository.getCachedLikeCount(any())).thenReturn(5);

        // when
        service.like(10L, 10L);

        // then
        verify(memoryRepository).addPostLike(10L, 10L);
        verify(memoryRepository).setLikeCount(any(), eq(6));
    }

    @Test
    @DisplayName("좋아요 취소 - 캐시에 count가 없는 경우")
    void cancelLikeNoCached() {
        // given
        when(memoryRepository.getCachedLikeCount(any())).thenReturn(-1);
        when(persistenceRepository.countByPostId(any())).thenReturn(5);

        // when
        service.cancelLike(10L, 10L);

        // then
        verify(memoryRepository).removePostLike(10L, 10L);
        verify(memoryRepository).setLikeCount(any(), eq(4));
    }

    @Test
    @DisplayName("좋아요 취소 - 캐시에 count가 있는 경우")
    void cancelLikeCached() {
        // given
        when(memoryRepository.getCachedLikeCount(any())).thenReturn(5);

        // when
        service.cancelLike(10L, 10L);

        // then
        verify(memoryRepository).removePostLike(10L, 10L);
        verify(memoryRepository).setLikeCount(any(), eq(4));
    }

    @Test
    @DisplayName("좋아요 확인 - 캐시에 좋아요가 등록된 경우")
    void isPostLikedCached() {
        // given
        when(memoryRepository.isPostLiked(any(), any())).thenReturn(true);

        // when
        boolean liked = service.isPostLiked(10L, 10L);

        // then
        assertThat(liked).isEqualTo(true);
    }

    @Test
    @DisplayName("좋아요 확인 - 캐시에 좋아요가 등록안되었고 DB에도 없는 경우")
    void isPostLikedNoCachedNoDB() {
        // given
        Optional<PostLike> result = Optional.empty();
        when(memoryRepository.isPostLiked(any(), any())).thenReturn(null);
        when(persistenceRepository.findByPostIdAndUserId(any(), any())).thenReturn(result);

        // when
        boolean liked = service.isPostLiked(10L, 10L);

        // then
        assertThat(liked).isEqualTo(false);
    }

    @Test
    @DisplayName("좋아요 확인 - 캐시에 좋아요가 등록안되었고 DB에는 있는 경우")
    void isPostLikedNoCached() {
        // given
        Optional<PostLike> result = Optional.of(new PostLike());
        when(memoryRepository.isPostLiked(any(), any())).thenReturn(null);
        when(persistenceRepository.findByPostIdAndUserId(any(), any())).thenReturn(result);

        // when
        boolean liked = service.isPostLiked(10L, 10L);

        // then
        assertThat(liked).isEqualTo(true);
    }

    @Test
    @DisplayName("좋아요 개수 확인 - 캐싱된 경우")
    void getCountOfLikesCached() {
        // given
        when(memoryRepository.getCachedLikeCount(any())).thenReturn(10);

        // when
        int likes = service.getCountOfLikes(10L);

        // then
        assertThat(likes).isEqualTo(10);
    }

    @Test
    @DisplayName("좋아요 개수 확인 - 캐싱안된 경우")
    void getCountOfLikesNoCached() {
        // given
        when(memoryRepository.getCachedLikeCount(any())).thenReturn(-1);
        when(persistenceRepository.countByPostId(any())).thenReturn(10);

        // when
        int likes = service.getCountOfLikes(10L);

        // then
        assertThat(likes).isEqualTo(10);
    }

    @Test
    @DisplayName("Memory에 캐시된 좋아요를 DB로 dump")
    void dumpToDB() {
        // given
        Stream<LikeEntry> likeStream = LongStream.range(0, 10)
                .mapToObj(i -> new LikeEntry(i, i, LikeState.LIKED));
        Stream<LikeEntry> cancelledStream = LongStream.range(10, 20)
                .mapToObj(i -> new LikeEntry(i, i, LikeState.CANCELLED));
        List<LikeEntry> likes = Stream.concat(likeStream, cancelledStream)
                .collect(Collectors.toList());

        when(memoryRepository.getAllPostLikes()).thenReturn(likes);
        when(userRepository.getReferenceById(any()))
                .thenAnswer(inv -> UserMock.createDummyMajor(inv.getArgument(0)));
        when(postRepository.getReferenceById(any()))
                .thenAnswer(inv -> NewsMock.createDummy(inv.getArgument(0)));

        // when
        service.dumpToDB();

        // then
        verify(persistenceRepository, times(10)).save(any(PostLike.class));

        for (int i = 10; i < 20; i++) {
            Long l = (long) i;
            verify(persistenceRepository).deleteByPostIdAndUserId(l, l);
        }
    }
}