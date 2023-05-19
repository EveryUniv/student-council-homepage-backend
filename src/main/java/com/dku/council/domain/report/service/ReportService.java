package com.dku.council.domain.report.service;

import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.model.dto.request.RequestCreateReportDto;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.post.PostRepository;
import com.dku.council.domain.report.exception.AlreadyReportedException;
import com.dku.council.domain.report.exception.CannotReportMineException;
import com.dku.council.domain.report.exception.PostedByAdminException;
import com.dku.council.domain.report.model.dto.list.ResponseReportCategoryDto;
import com.dku.council.domain.report.model.dto.response.ResponseReportCategoryCountDto;
import com.dku.council.domain.report.model.dto.response.ResponseSingleReportedPostDto;
import com.dku.council.domain.report.model.dto.response.SummarizedReportedPostDto;
import com.dku.council.domain.report.model.entity.Report;
import com.dku.council.domain.report.model.entity.ReportCategory;
import com.dku.council.domain.report.repository.ReportRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MessageSource messageSource;

    @Value("${app.report.count}")
    private final int reportCountThreshold;

    @Transactional
    public void report(Long postId, Long userId, RequestCreateReportDto dto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        if (reportRepository.existsByUserIdAndPostId(user.getId(), post.getId())) {
            throw new AlreadyReportedException();
        }

        if (post.getUser().getUserRole().isAdmin()) {
            throw new PostedByAdminException();
        } else if (post.getUser().getId().equals(user.getId())) {
            throw new CannotReportMineException();
        }

        Report report = dto.toEntity(user, post);
        reportRepository.save(report);

        if (reportRepository.countByPostId(postId) >= reportCountThreshold) {
            post.blind();
        }
    }

    public List<ResponseReportCategoryDto> getCategoryNames() {
        List<ReportCategory> categories = Arrays.asList(ReportCategory.values());
        return categories.stream()
                .map(category -> new ResponseReportCategoryDto(category, messageSource))
                .collect(Collectors.toList());
    }

    public Page<SummarizedReportedPostDto> getReportedPosts(Pageable pageable) {
        return reportRepository.findAllReportedPosts(pageable)
                .map(SummarizedReportedPostDto::new);
    }

    public ResponseSingleReportedPostDto getReportedPost(Long userId, Long postId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (!user.getUserRole().isAdmin()) {
            throw new UserNotFoundException();
        }

        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        List<Report> reports = post.getReports();
        List<ResponseReportCategoryCountDto> categories = new ArrayList<>();
        for (ReportCategory category : ReportCategory.values()) {
            long count = reports.stream().filter(report -> report.getReportCategory() == category).count();
            if (count != 0) {
                categories.add(new ResponseReportCategoryCountDto(category.getName(messageSource), count));
            }
        }


        Long reportedCount = reportRepository.countByPostId(postId);
        return new ResponseSingleReportedPostDto(post, reportedCount, categories);
    }
}
