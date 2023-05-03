package com.dku.council.domain.user.repository;

import com.dku.council.domain.user.model.entity.NicknameFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NicknameFilterRepositoryTest {

    @Autowired
    private NicknameFilterRepository repository;

    @BeforeEach
    void setup() {
        List<String> words = List.of("바보", "바부", "멍청이", "Shit", "admin");
        repository.saveAll(words.stream()
                .map(NicknameFilter::new)
                .collect(Collectors.toList()));
    }

    @Test
    @DisplayName("매칭되는 필터 개수 정확히 반환하는지?")
    void countMatchedFilter() {
        // when
        Long result1 = repository.countMatchedFilter("야이 바보야");
        Long result2 = repository.countMatchedFilter("야이 멍청아");
        Long result3 = repository.countMatchedFilter("shit");
        Long result4 = repository.countMatchedFilter("바보멍청이");
        Long result5 = repository.countMatchedFilter("X멍청이XaDmInX");
        Long result6 = repository.countMatchedFilter("하이요");
        Long result7 = repository.countMatchedFilter("바1보");
        Long result8 = repository.countMatchedFilter("야이 멍청이야");

        // then
        assertThat(result1).isEqualTo(1L);
        assertThat(result2).isEqualTo(0L);
        assertThat(result3).isEqualTo(1L);
        assertThat(result4).isEqualTo(2L);
        assertThat(result5).isEqualTo(2L);
        assertThat(result6).isEqualTo(0L);
        assertThat(result7).isEqualTo(0L);
        assertThat(result8).isEqualTo(1L);
    }
}