package com.dku.council.domain.report.controller;

import com.dku.council.domain.post.model.dto.request.RequestCreateReportDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.report.model.dto.list.ResponseReportCategoryDto;
import com.dku.council.domain.report.model.dto.response.ResponseSingleReportedPostDto;
import com.dku.council.domain.report.model.dto.response.SummarizedReportedPostDto;
import com.dku.council.domain.report.service.ReportService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.AdminAuth;
import com.dku.council.global.auth.role.UserAuth;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "신고", description = "신고 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    /**
     * 게시글 신고
     *
     * @param id  게시글 id
     * @param dto 신고 카테고리 id
     */
    @PostMapping("{id}")
    @UserAuth
    public void report(@PathVariable Long id, AppAuthentication auth, RequestCreateReportDto dto) {
        reportService.report(id, auth.getUserId(), dto);
    }

    /**
     * 신고 카테고리 목록 조회
     */
    @GetMapping("/category")
    public List<ResponseReportCategoryDto> getReportCategory() {
        return reportService.getCategoryNames();
    }

    /**
     * 신고된 게시글 목록 조회 (Admin)
     *
     * @param pageable 페이징 정보
     */
    @GetMapping
    @AdminAuth
    public ResponsePage<SummarizedReportedPostDto> getReportedPosts(@ParameterObject Pageable pageable) {
        Page<SummarizedReportedPostDto> reportedPosts = reportService.getReportedPosts(pageable);
        return new ResponsePage<>(reportedPosts);
    }

    /**
     * 신고된 게시글 상세 조회 (Admin)
     */
    @GetMapping("/{id}")
    @AdminAuth
    public ResponseSingleReportedPostDto getReportedPost(AppAuthentication auth, @PathVariable Long id) {
        return reportService.getReportedPost(auth.getUserId(), id);
    }
}
