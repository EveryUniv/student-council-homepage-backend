package com.dku.council.domain.report.service;

import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.model.dto.request.RequestCreateReportDto;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.PostRepository;
import com.dku.council.domain.report.exception.AlreadyReportedException;
import com.dku.council.domain.report.model.entity.Report;
import com.dku.council.domain.report.model.entity.ReportCategory;
import com.dku.council.domain.report.repository.ReportRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MessageSource messageSource;

    @Value("${app.report.count}")
    private final int REPORT_COUNT;
    @Transactional
    public void report(Long postId, Long userId, RequestCreateReportDto dto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        if (reportRepository.existsByUserIdAndPostId(user.getId(), post.getId())) {
            throw new AlreadyReportedException();
        }

        Report report = dto.toEntity(user, post);

        reportRepository.save(report);

        if(reportRepository.countByPostId(postId) >= REPORT_COUNT) {
            post.blind();
        }
    }
}
