package com.dku.council.domain.post.repository.post;

import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.GeneralForumMock;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.NewsMock;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GeneralForumRepositoryTest {

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeneralForumRepository repository;

    @Autowired
    private NewsRepository newsRepository;

    private User user1;

    @BeforeEach
    public void setup() {
        Major major = MajorMock.create();
        major = majorRepository.save(major);

        user1 = UserMock.create(major);
        user1 = userRepository.save(user1);

        User user2 = UserMock.create(major);
        user2 = userRepository.save(user2);

        List<GeneralForum> posts = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            User user = user2; // 다른 유저가 작성한 게시글
            if (i >= 15) {
                user = user1;
            }

            GeneralForum post = GeneralForumMock.create(user);
            if (i >= 20) { // blind된 게시글
                post.blind();
            }

            posts.add(post);
        }
        repository.saveAll(posts);

        // 다른 타입의 게시글
        newsRepository.saveAll(List.of(
                NewsMock.create(user1),
                NewsMock.create(user1),
                NewsMock.create(user1),
                NewsMock.create(user1)
        ));
    }

    @Test
    @DisplayName("findAllByUserId - Active 상태인 Post만 가져오는가")
    void findAllByUserId() {
        // when
        Page<GeneralForum> posts = repository.findAllByUserId(user1.getId(), Pageable.unpaged());

        // then
        assertThat(posts.getTotalElements()).isEqualTo(5);
    }
}