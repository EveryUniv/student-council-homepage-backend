package com.dku.council.domain.statistic.service;

import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.repository.post.PetitionRepository;
import com.dku.council.domain.statistic.model.entity.PetitionStatistic;
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
    public void setup() {
        service = new PetitionStatisticService(petitionStatisticRepository, userRepository, petitionRepository);
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

    @Test
    @DisplayName("이미 동의한 유저인지 확인")
    void isAlreadyAgreed() {
        // given
        Petition petition = PetitionMock.createWithDummy();
        PetitionStatistic stat = PetitionStatisticMock.create(petition);
        when(petitionStatisticRepository.countByPetitionIdAndUserId(petition.getId(), stat.getUser().getId()))
                .thenReturn(1L);

        // when & then
        assertThat(service.isAlreadyAgreed(petition.getId(), stat.getUser().getId())).isTrue();
    }
}