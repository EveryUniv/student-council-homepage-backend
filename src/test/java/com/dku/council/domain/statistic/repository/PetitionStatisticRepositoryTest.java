package com.dku.council.domain.statistic.repository;

import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.repository.post.PetitionRepository;
import com.dku.council.domain.statistic.model.dto.PetitionStatisticDto;
import com.dku.council.domain.statistic.model.entity.PetitionStatistic;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.PetitionMock;
import com.dku.council.mock.PetitionStatisticMock;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PetitionStatisticRepositoryTest {

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetitionRepository petitionRepository;

    @Autowired
    private PetitionStatisticRepository repository;

    private Petition petition;


    @BeforeEach
    public void setup() {
        List<Major> majors = MajorMock.createList(5);
        majors = majorRepository.saveAll(majors);

        List<User> users = new ArrayList<>(majors.size());
        for (Major major : majors) {
            users.add(UserMock.create(major));
        }
        users = userRepository.saveAll(users);

        LocalDateTime now = LocalDateTime.of(2022, 2, 2, 2, 2);
        petition = PetitionMock.create(users.get(0), now);
        petition = petitionRepository.save(petition);

        List<PetitionStatistic> list = PetitionStatisticMock.list(users, petition);
        repository.saveAll(list);
    }


    @Test
    void find4CountGroupByDepartment() {
        //when
        PageRequest page = PageRequest.of(0, 4);
        List<PetitionStatisticDto> top4Department = repository.findCountGroupByDepartment(petition.getId(), page);

        //then
        assertThat(top4Department).hasSize(4);
        assertThat(top4Department.get(0).getDepartment()).isEqualTo("MyDepartment1");
        assertThat(top4Department.get(0).getAgreeCount()).isEqualTo(100);
        assertThat(top4Department.get(1).getDepartment()).isEqualTo("MyDepartment4");
        assertThat(top4Department.get(1).getAgreeCount()).isEqualTo(41);
        assertThat(top4Department.get(2).getDepartment()).isEqualTo("MyDepartment2");
        assertThat(top4Department.get(2).getAgreeCount()).isEqualTo(40);
        assertThat(top4Department.get(3).getDepartment()).isEqualTo("MyDepartment3");
        assertThat(top4Department.get(3).getAgreeCount()).isEqualTo(39);
    }
}