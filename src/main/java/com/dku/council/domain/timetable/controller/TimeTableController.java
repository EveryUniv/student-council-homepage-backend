package com.dku.council.domain.timetable.controller;

import com.dku.council.domain.timetable.model.dto.LectureDto;
import com.dku.council.domain.timetable.model.dto.TimeTableRequestDto;
import com.dku.council.domain.timetable.service.TimeTableService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.UserOnly;
import com.dku.council.global.dto.ResponseIdDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "시간표", description = "수업 시간표 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/timetable")
public class TimeTableController {

    private final TimeTableService timeTableService;

    /**
     * 내 시간표 조회
     * <p>시간표 이름을 입력받아 해당 시간표를 가져옵니다.</p>
     *
     * @param name 시간표 이름
     * @return 시간표 수업 목록
     */
    @GetMapping
    @UserOnly
    public List<LectureDto> list(AppAuthentication auth, @RequestParam String name) {
        return timeTableService.list(auth.getUserId(), name);
    }

    /**
     * 시간표 추가
     * <p>시간표 이름을 입력받아 시간표를 추가합니다. 같은 이름의 시간표가 이미 있는 경우에는
     * 오류가 발생합니다.</p>
     *
     * @param dto 요청 body
     */
    @PostMapping
    @UserOnly
    public ResponseIdDto create(AppAuthentication auth, @Valid @RequestBody TimeTableRequestDto dto) {
        Long id = timeTableService.create(auth.getUserId(), dto);
        return new ResponseIdDto(id);
    }

    /**
     * 시간표 변경
     * <p>시간표 이름을 입력받아 시간표를 변경합니다. 시간표를 찾을 수 없는 경우 오류가
     * 발생합니다.</p>
     *
     * @param dto 요청 body
     */
    @PatchMapping
    @UserOnly
    public ResponseIdDto update(AppAuthentication auth, @Valid @RequestBody TimeTableRequestDto dto) {
        Long id = timeTableService.update(auth.getUserId(), dto);
        return new ResponseIdDto(id);
    }

    /**
     * 시간표 삭제
     * <p>해당 이름의 시간표를 삭제합니다.</p>
     *
     * @param name 시간표 이름
     */
    @DeleteMapping
    @UserOnly
    public ResponseIdDto delete(AppAuthentication auth, @RequestParam String name) {
        Long id = timeTableService.delete(auth.getUserId(), name);
        return new ResponseIdDto(id);
    }
}
