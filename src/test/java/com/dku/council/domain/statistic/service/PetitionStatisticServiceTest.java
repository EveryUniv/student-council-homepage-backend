package com.dku.council.domain.statistic.service;

import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.repository.PetitionRepository;
import com.dku.council.domain.statistic.PetitionStatistic;
import com.dku.council.domain.statistic.model.dto.PetitionStatisticDto;
import com.dku.council.domain.statistic.repository.PetitionStatisticRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.PetitionMock;
import com.dku.council.mock.PetitionStatisticMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetitionStatisticServiceTest {

    @Mock
    private PetitionStatisticRepository petitionStatisticRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PetitionRepository petitionRepository;

    private PetitionStatisticService service;

    @BeforeEach
    public void setup(){
        service = new PetitionStatisticService(petitionStatisticRepository, userRepository, petitionRepository);
    }

    @Test
    @DisplayName("상위 4개의 데이터를 잘 가져오는지")
    void findTop4DepartmentTest() {
        //given
        Petition petition = PetitionMock.createWithDummy();
        List<PetitionStatistic> list = PetitionStatisticMock.list(petition);
        when(petitionStatisticRepository.findAllByPetitionId(petition.getId())).thenReturn(list);

        //when
        List<PetitionStatisticDto> top4Department = service.findTop4Department(petition.getId());
        //then
        assertThat(top4Department.size()).isEqualTo(4);
        assertThat(top4Department.get(0).getAgreeCount()).isEqualTo(100);
        assertThat(top4Department.get(1).getAgreeCount()).isEqualTo(41);
        assertThat(top4Department.get(2).getAgreeCount()).isEqualTo(40);
        assertThat(top4Department.get(3).getAgreeCount()).isEqualTo(39);
    }

    @Test
    @DisplayName("총 동의 수를 잘 가져오는지")
    void getTotalAgreeCount() {
        //given
        Petition petition = PetitionMock.createWithDummy();
        List<PetitionStatistic> list = PetitionStatisticMock.list(petition);
        when(petitionStatisticRepository.countByPetitionId(petition.getId())).thenReturn(list.size());

        //when  &  then
        assertThat(service.count(petition.getId())).isEqualTo(list.size());
    }
}