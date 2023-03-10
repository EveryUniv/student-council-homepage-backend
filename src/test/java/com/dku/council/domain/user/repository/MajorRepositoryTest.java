package com.dku.council.domain.user.repository;

import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.util.FieldInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MajorRepositoryTest {

    @Autowired
    private MajorRepository majorRepository;

    private List<Major> majors;

    @BeforeEach
    public void setup() {
        majors = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Major major = new Major("MAJOR" + i, "DEP" + (i / 2));
            if (i >= 10) {
                FieldInjector.inject(Major.class, major, "isActive", false);
            }
            majors.add(major);
        }
        majors = majorRepository.saveAll(majors);
    }

    @Test
    @DisplayName("학과명, 소속대학명으로 Major를 잘 찾을 수 있는가")
    void findByNameAndDepartmentName() {
        // when
        Optional<Major> actual1 = majorRepository.findByName("MAJOR5", "DEP1");
        Optional<Major> actual2 = majorRepository.findByName("MAJOR3", "DEP1");
        Optional<Major> actual3 = majorRepository.findByName("MAJOR6", "DEP3");
        Optional<Major> actual4 = majorRepository.findByName("MAJOR8", "DEP00");
        Optional<Major> actual5 = majorRepository.findByName("MAJOR10", "DEP5");
        Optional<Major> actual6 = majorRepository.findByName("MAJOR15", "DEP2");

        // then
        assertThat(actual1.isEmpty()).isTrue();
        assertThat(actual2.orElseThrow().getName()).isEqualTo("MAJOR3");
        assertThat(actual3.orElseThrow().getDepartment()).isEqualTo("DEP3");
        assertThat(actual4.isEmpty()).isTrue();
        assertThat(actual5.isEmpty()).isTrue();
        assertThat(actual6.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("활성화된 Major만 잘 찾을 수 있는가")
    void findAllWithActivated() {
        // when
        List<Major> majors = majorRepository.findAll();

        // then
        assertThat(majors.size()).isEqualTo(10);
        for (Major major : majors) {
            assertThat(major.isActive()).isTrue();
        }
    }
}