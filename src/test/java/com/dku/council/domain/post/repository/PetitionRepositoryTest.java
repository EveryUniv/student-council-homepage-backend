package com.dku.council.domain.post.repository;

import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.repository.post.GeneralForumRepository;
import com.dku.council.domain.post.repository.post.PetitionRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PetitionRepositoryTest {

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetitionRepository repository;

    @Autowired
    private GeneralForumRepository generalForumRepository;

    private User user1;
    private final LocalDateTime baseTime = LocalDateTime.of(2022, 2, 2, 2, 2);

    @BeforeEach
    public void setup() {
        Major major = MajorMock.create();
        major = majorRepository.save(major);

        user1 = UserMock.create(major);
        user1 = userRepository.save(user1);

        User user2 = UserMock.create(major);
        user2 = userRepository.save(user2);

        List<Petition> petitions = new ArrayList<>();
        LocalDateTime now = baseTime;
        for (int i = 0; i < 30; i++) {
            now = now.plus(Duration.ofDays(2));
            User user = user2; // 다른 유저가 작성한 게시글
            if (i >= 15) {
                user = user1;
            }

            Petition petition = PetitionMock.create(user, now);
            if (i >= 20) { // blind된 게시글
                petition.blind();
            }

            petitions.add(petition);
        }
        repository.saveAll(petitions);

        // 다른 타입의 게시글
        generalForumRepository.saveAll(List.of(
                GeneralForumMock.create(user1),
                GeneralForumMock.create(user1),
                GeneralForumMock.create(user1),
                GeneralForumMock.create(user1)
        ));
    }

    @Test
    @DisplayName("기간이 만료된 청원을 잘 업데이트 하는가")
    void updateExpiredPetition() {
        // given
        int count = 8;
        LocalDateTime lessThan = baseTime.plus(Duration.ofDays(2 * count)).minusHours(1);

        // when
        repository.updateExpiredPetition(lessThan);

        // then
        repository.findAll().forEach(petition -> {
            if (petition.getCreatedAt().isBefore(lessThan)) {
                assertThat(petition.getExtraStatus()).isEqualTo(PetitionStatus.EXPIRED);
            } else {
                assertThat(petition.getExtraStatus()).isEqualTo(PetitionStatus.ACTIVE);
            }
        });
    }

    @Test
    @DisplayName("findAllByUserId - Active 상태인 Post만 가져오는가")
    void findAllByUserId() {
        // when
        Page<Petition> posts = repository.findAllByUserId(user1.getId(), Pageable.unpaged());

        // then
        assertThat(posts.getTotalElements()).isEqualTo(5);
    }
}