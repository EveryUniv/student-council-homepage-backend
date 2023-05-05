package com.dku.council.domain.post.repository.post;

import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.GeneralForumMock;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.PetitionMock;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetitionRepository petitionRepository;

    @Autowired
    private GeneralForumRepository generalForumRepository;

    @Autowired
    private PostRepository repository;

    private User user1, user2;

    @BeforeEach
    public void setup() {
        Major major = MajorMock.create();
        major = majorRepository.save(major);

        user1 = UserMock.create(major);
        user1 = userRepository.save(user1);

        user2 = UserMock.create(major);
        user2 = userRepository.save(user2);

        LocalDateTime baseTime = LocalDateTime.of(2022, 2, 2, 2, 2);
        petitionRepository.saveAll(newPosts(user -> PetitionMock.create(user, baseTime)));
        generalForumRepository.saveAll(newPosts(GeneralForumMock::create));
    }

    private <T extends Post> List<T> newPosts(Function<User, T> postFactory) {
        List<T> posts = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            User user = user2; // 다른 유저가 작성한 게시글
            if (i >= 15) {
                user = user1;
            }

            T post = postFactory.apply(user);
            if (i >= 20) { // blind된 게시글
                post.blind();
            }

            posts.add(post);
        }
        return posts;
    }


    @Test
    @DisplayName("내가 작성한 게시글 개수를 정확하게 카운팅하는지")
    void countAllByUserId() {
        // when
        Long counts = repository.countAllByUserId(user1.getId());

        // then
        assertThat(counts).isEqualTo(5L);
    }
}