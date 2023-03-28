package com.dku.council.domain.statistic.service;

import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.repository.PetitionRepository;
import com.dku.council.domain.statistic.PetitionStatistic;
import com.dku.council.domain.statistic.model.dto.PetitionStatisticDto;
import com.dku.council.domain.statistic.repository.PetitionStatisticRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.PetitionMock;
import com.dku.council.mock.PetitionStatisticMock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
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
        when(petitionStatisticRepository.findAllByPetition(petition.getId())).thenReturn(list);

        //when
        PetitionStatisticDto top4Department = service.findTop4Department(petition.getId());
        //then

        assertThat(top4Department.getTop4Department().size()).isEqualTo(4);
        assertThat(top4Department.getTop4Department().get(0).getValue()).isEqualTo(100);
        assertThat(top4Department.getTop4Department().get(1).getValue()).isEqualTo(41);
        assertThat(top4Department.getTop4Department().get(2).getValue()).isEqualTo(40);
        assertThat(top4Department.getTop4Department().get(3).getValue()).isEqualTo(39);

    }
}