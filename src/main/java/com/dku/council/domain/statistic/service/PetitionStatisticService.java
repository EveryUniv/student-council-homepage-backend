package com.dku.council.domain.statistic.service;

import com.dku.council.domain.post.repository.PetitionRepository;
import com.dku.council.domain.post.repository.PostRepository;
import com.dku.council.domain.statistic.PetitionStatistic;
import com.dku.council.domain.statistic.model.dto.PetitionStatisticDto;
import com.dku.council.domain.statistic.repository.PetitionStatisticRepository;
import com.dku.council.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PetitionStatisticService {

    private final PetitionStatisticRepository repository;
    private final UserRepository userRepository;

    /**
     * petitionId 로 저장되어 있는 Department 를 조회하여 가장 많은 Department 4개를 조회한다.
     *
     * @param petitionId
     * @return
     */
    public PetitionStatisticDto findTop4Department(Long petitionId){
        List<PetitionStatistic> petitionStatisticList = repository.findAllByPostId(petitionId);

        Stream<String> departmentList = petitionStatisticList.stream().map(PetitionStatistic::getDepartment);

        Map<String, Integer> collect = departmentList.collect(
                Collectors.toMap(Function.identity(), value -> 1, Integer::sum)
        );

        List<Map.Entry<String, Integer>> top4Department = collect.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(4)
                .collect(Collectors.toList());

        return new PetitionStatisticDto(top4Department);
    }
}
