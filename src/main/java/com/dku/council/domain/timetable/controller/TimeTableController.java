package com.dku.council.domain.timetable.controller;

import com.dku.council.domain.timetable.model.dto.TimeTableDto;
import com.dku.council.global.auth.jwt.AppAuthentication;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "시간표", description = "수업 시간표 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/timetable")
public class TimeTableController {

    /**
     * 내 시간표를 조회
     * <p>시간표 이름을 입력받아 해당 시간표를 가져옵니다.</p>
     *
     * @param name 시간표 이름
     * @return 시간표 수업 목록
     */
    @GetMapping
    public List<TimeTableDto> list(AppAuthentication auth,
                                   @RequestParam String name) {
        return null;
    }

    /**
     * 시간표를 추가
     * <p>시간표 이름을 입력받아 시간표를 추가합니다.</p>
     *
     * @param name 시간표 이름
     */
    @PostMapping
    public void create(AppAuthentication auth,
                       @RequestParam String name) {
    }
}
