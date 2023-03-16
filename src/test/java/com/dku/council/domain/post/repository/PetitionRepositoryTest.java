package com.dku.council.domain.post.repository;

import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.PetitionMock;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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

    private final LocalDateTime baseTime = LocalDateTime.of(2022, 2, 2, 2, 2);
    private List<Petition> petitions;

    @BeforeEach
    public void setup() {
        Major major = MajorMock.create();
        major = majorRepository.save(major);

        User user = UserMock.create(major);
        user = userRepository.save(user);

        petitions = new ArrayList<>();
        LocalDateTime now = baseTime;
        for (int i = 0; i < 20; i++) {
            now = now.plus(Duration.ofDays(2));
            Petition petition = PetitionMock.create(user, now);
            petitions.add(petition);
        }
        petitions = repository.saveAll(petitions);
    }

    @Test
    @DisplayName("최근 5개의 청원을 잘 가져오는가")
    void findTop5ByOrderByCreatedAtDesc() {
        // when
        List<Petition> result = repository.findTop5ByOrderByCreatedAtDesc();

        // then
        assertThat(result.size()).isEqualTo(5);
        assertThat(result).containsExactlyInAnyOrderElementsOf(petitions.subList(15, 20));
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
}