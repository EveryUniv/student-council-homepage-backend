package com.dku.council.domain.mainpage.controller;

import com.dku.council.domain.mainpage.model.dto.request.RequestCarouselImageDto;
import com.dku.council.domain.mainpage.model.dto.response.CarouselImageResponse;
import com.dku.council.domain.mainpage.model.dto.response.MainPageResponseDto;
import com.dku.council.domain.mainpage.model.dto.response.ScheduleResponseDto;
import com.dku.council.domain.mainpage.service.MainPageService;
import com.dku.council.domain.mainpage.service.ScheduleService;
import com.dku.council.global.auth.role.AdminAuth;
import com.dku.council.global.config.jackson.JacksonDateTimeFormatter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "메인 페이지", description = "메인 페이지 관련 api")
@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainPageController {
    private final MainPageService mainPageService;
    private final ScheduleService scheduleService;

    /**
     * 메인 페이지 화면 데이터. 캐러셀이미지 & 총학소식 & 청원 & 회의록
     *
     * @return 총학소식, 청원, 회의록 최신 5개의 데이터를 반환합니다.
     */
    @GetMapping
    public MainPageResponseDto index() {
        return mainPageService.mainPageInfo();
    }

    /**
     * 일정 가져오기
     * <p>시작일부터 종료일까지의 일정을 반환합니다. (시작일, 종료일 포함)</p>
     *
     * @param from 시작일
     * @param to   종료일
     * @return 조건에 맞는 일정들을 반환합니다.
     */
    @GetMapping("/schedule")
    public List<ScheduleResponseDto> schedule(
            @RequestParam @DateTimeFormat(pattern = JacksonDateTimeFormatter.DATE_FORMAT_PATTERN) LocalDate from,
            @RequestParam @DateTimeFormat(pattern = JacksonDateTimeFormatter.DATE_FORMAT_PATTERN) LocalDate to) {
        return scheduleService.getSchedules(from, to);
    }

    /**
     * 캐러셀 이미지 업로드 ONLY FOR ADMIN
     *
     * @param dto 이미지 파일 & 리다이렉트 URL
     */
    @PostMapping(value = "/carousel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AdminAuth
    public void uploadCarouselImage(@Valid @ModelAttribute RequestCarouselImageDto dto) {
        mainPageService.addCarouselImage(dto);
    }

    /**
     * 캐러셀 목록 가져오기
     *
     * @return 저장되어 있는 모든 캐러셀 이미지 파일을 반환합니다.
     */
    @GetMapping("/carousel")
    public List<CarouselImageResponse> getCarouselImages() {
        return mainPageService.getCarouselImages();
    }

    /**
     * 캐러셀 삭제
     *
     * @param carouselId 캐러셀 ID로 삭제합니다. ONLY FOR ADMIN
     */
    @DeleteMapping("/carousel/{id}")
    @AdminAuth
    public void deleteCarouselImage(@PathVariable("id") Long carouselId) {
        mainPageService.deleteCarouselImage(carouselId);
    }
}
