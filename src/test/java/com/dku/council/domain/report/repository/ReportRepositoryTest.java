package com.dku.council.domain.report.repository;

import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.post.repository.post.GeneralForumRepository;
import com.dku.council.domain.report.model.dto.response.SummarizedReportedPostDto;
import com.dku.council.domain.report.model.entity.Report;
import com.dku.council.domain.report.model.entity.ReportCategory;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.GeneralForumMock;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReportRepositoryTest {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeneralForumRepository generalForumRepository;

    private List<GeneralForum> posts;

    @BeforeEach
    public void setup() {
        Major major = MajorMock.create();
        major = majorRepository.save(major);

        final List<User> users = new ArrayList<>(30);

        for (int i = 0; i < 30; i++) {
            User user = UserMock.create(major);
            user = userRepository.save(user);
            users.add(user);
        }

        posts = generalForumRepository.saveAll(List.of(
                GeneralForumMock.create(users.get(0)),
                GeneralForumMock.create(users.get(1)),
                GeneralForumMock.create(users.get(2)),
                GeneralForumMock.create(users.get(3))
        ));

        ReportCategory[] values = ReportCategory.values();
        for (int i = 0; i < 10; i++) {
            reportRepository.save(new Report(users.get(i), posts.get(0), values[i % values.length]));
        }
        for (int i = 0; i < 10; i++) {
            reportRepository.save(new Report(users.get(i + 10), posts.get(1), values[i % values.length]));
        }
    }

    @Test
    @DisplayName("신고된 게시글이 잘 출력되는지?")
    void findAllReportedPosts() {
        // when
        Page<SummarizedReportedPostDto> posts = reportRepository.findAllReportedPosts(Pageable.unpaged())
                .map(SummarizedReportedPostDto::new);

        // then
        assertThat(posts).containsExactlyInAnyOrderElementsOf(
                List.of(
                        new SummarizedReportedPostDto(this.posts.get(0)),
                        new SummarizedReportedPostDto(this.posts.get(1))
                )
        );
    }
}