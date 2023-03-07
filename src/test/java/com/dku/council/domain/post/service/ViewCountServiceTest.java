package com.dku.council.domain.post.service;

import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.ViewCountMemoryRepository;
import com.dku.council.util.ClockUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewCountServiceTest {

    private final Clock clock = ClockUtil.create();
    private ViewCountService service;

    @Mock
    private ViewCountMemoryRepository memoryRepository;

    @Mock
    private Post post;


    @BeforeEach
    public void setup() {
        this.service = new ViewCountService(memoryRepository, clock, 10);
    }

    @Test
    @DisplayName("조회수가 잘 올라가는지? - 최근에 조회한 적 없는 경우")
    void increasePostViewsNoCached() {
        // given
        when(memoryRepository.isAlreadyContains(any(), any(), any())).thenReturn(false);

        // when
        service.increasePostViews(post, "Address");

        // then
        verify(post).increaseViewCount();
    }

    @Test
    @DisplayName("조회수가 잘 올라가는지? - 최근에 조회한 적 있는 경우")
    void increasePostViewsCached() {
        // given
        when(memoryRepository.isAlreadyContains(any(), any(), any())).thenReturn(true);

        // when
        service.increasePostViews(post, "Address");

        // then
        verify(post, never()).increaseViewCount();
    }
}