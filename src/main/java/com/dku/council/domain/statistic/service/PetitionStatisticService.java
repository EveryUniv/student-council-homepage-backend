package com.dku.council.domain.statistic.service;

import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.repository.post.PetitionRepository;
import com.dku.council.domain.statistic.model.dto.PetitionStatisticDto;
import com.dku.council.domain.statistic.model.entity.PetitionStatistic;
import com.dku.council.domain.statistic.repository.PetitionStatisticRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PetitionStatisticService {

    private final PetitionStatisticRepository repository;
    private final UserRepository userRepository;
    private final PetitionRepository petitionRepository;

    /**
     * petitionId 로 저장되어 있는 Department 를 조회하여 가장 많은 Department 4개를 조회한다.
     *
     * @param petitionId 조회할 petitionId
     * @return Department 4개
     */
    public List<PetitionStatisticDto> findTop4Department(Long petitionId) {
        PageRequest pageable = PageRequest.of(0, 4);
        return repository.findCountGroupByDepartment(petitionId, pageable);
    }

    /**
     * 동의 통계 테이블에 저장합니다.
     *
     * @param postId 게시글 id
     * @param userId 사용자 id
     */
    public void save(Long postId, Long userId) {
        Petition petition = petitionRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        PetitionStatistic statistic = PetitionStatistic.builder()
                .petition(petition)
                .user(user)
                .build();
        repository.save(statistic);
    }

    public int count(Long postId) {
        return repository.countByPetitionId(postId);
    }

    public boolean isAlreadyAgreed(Long postId, Long userId) {
        return repository.countByPetitionIdAndUserId(postId, userId) > 0;
    }
}
