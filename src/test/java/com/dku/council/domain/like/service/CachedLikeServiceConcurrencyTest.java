package com.dku.council.domain.like.service;

import com.dku.council.domain.like.repository.LikeMemoryRepository;
import com.dku.council.domain.like.service.impl.CachedLikeServiceImpl;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.post.NewsRepository;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.NewsMock;
import com.dku.council.mock.UserMock;
import com.dku.council.util.base.AbstractContainerRedisTest;
import com.dku.council.util.test.FullIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.dku.council.domain.like.model.LikeTarget.POST;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@FullIntegrationTest
class CachedLikeServiceConcurrencyTest extends AbstractContainerRedisTest {

    private static final int THREAD_COUNT = 100;

    @Autowired
    private CachedLikeServiceImpl service;

    @Autowired
    private LikeMemoryRepository memoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private NewsRepository newsRepository;

    private Post post;
    private List<User> users;


    @BeforeEach
    void setUp() {
        Major major = majorRepository.save(MajorMock.create());
        users = new ArrayList<>(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            User user = UserMock.create(major);
            user = userRepository.save(user);
            users.add(user);
        }

        post = newsRepository.save(NewsMock.create(users.get(0)));
    }

    @Test
    @DisplayName("동시에 k건 이상의 like를 추가하면, k건만큼 like가 추가되어야 한다.")
    void likeNoCached() throws InterruptedException {
        // given
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int index = i;
            new Thread(() -> {
                service.like(post.getId(), users.get(index).getId(), POST);
                latch.countDown();
            }).start();
        }
        latch.await();

        // then
        assertThat(service.getCountOfLikes(post.getId(), POST)).isEqualTo(THREAD_COUNT);
        assertThat(memoryRepository.getAllLikesAndClear(POST).size()).isEqualTo(THREAD_COUNT);
    }
}