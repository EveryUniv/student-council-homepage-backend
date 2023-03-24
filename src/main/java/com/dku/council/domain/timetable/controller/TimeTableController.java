package com.dku.council.domain.timetable.controller;

import com.dku.council.domain.timetable.model.dto.request.CreateTimeTableRequestDto;
import com.dku.council.domain.timetable.model.dto.request.UpdateTimeTableNameRequestDto;
import com.dku.council.domain.timetable.model.dto.request.UpdateTimeTableRequestDto;
import com.dku.council.domain.timetable.model.dto.response.TimeTableDto;
import com.dku.council.domain.timetable.model.dto.response.TimeTableInfoDto;
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
     * 내 시간표 목록 조회
     * <p>시간표 이름 목록을 가져옵니다.</p>
     *
     * @return 시간표 목록
     */
    @GetMapping
    @UserOnly
    public List<TimeTableInfoDto> list(AppAuthentication auth) {
        return timeTableService.list(auth.getUserId());
    }

    /**
     * 내 시간표 단건 조회
     * <p>시간표에 어떤 수업이 있는지 구체적으로 조회합니다.</p>
     *
     * @param tableId 시간표 ID
     * @return 시간표 수업 목록
     */
    @GetMapping("/{tableId}")
    @UserOnly
    public TimeTableDto findOne(AppAuthentication auth, @PathVariable Long tableId) {
        return timeTableService.findOne(auth.getUserId(), tableId);
    }

    /**
     * 시간표 추가
     * <p>시간표 이름을 입력받아 시간표를 추가합니다. 같은 이름의 시간표가 이미 있는 경우에는
     * 오류가 발생합니다.</p>
     *
     * @param dto 요청 body
     * @return 생성된 시간표 ID
     */
    @PostMapping
    @UserOnly
    public ResponseIdDto create(AppAuthentication auth, @Valid @RequestBody CreateTimeTableRequestDto dto) {
        Long id = timeTableService.create(auth.getUserId(), dto);
        return new ResponseIdDto(id);
    }

    /**
     * 시간표 변경
     * <p>시간표 이름을 입력받아 시간표를 변경합니다. 시간표를 찾을 수 없는 경우 오류가
     * 발생합니다.</p>
     *
     * @param dto 요청 body
     * @return 변경된 시간표 ID
     */
    @PatchMapping("/{tableId}")
    @UserOnly
    public ResponseIdDto update(AppAuthentication auth,
                                @PathVariable Long tableId,
                                @Valid @RequestBody UpdateTimeTableRequestDto dto) {
        Long id = timeTableService.update(auth.getUserId(), tableId, dto.getLectures());
        return new ResponseIdDto(id);
    }

    /**
     * 시간표 이름 변경
     * <p>시간표 이름을 변경합니다.</p>
     *
     * @param dto 요청 body
     * @return 변경된 시간표 ID
     */
    @PatchMapping("/name/{tableId}")
    @UserOnly
    public ResponseIdDto updateName(AppAuthentication auth,
                                    @PathVariable Long tableId,
                                    @Valid @RequestBody UpdateTimeTableNameRequestDto dto) {
        Long id = timeTableService.updateName(auth.getUserId(), tableId, dto.getName());
        return new ResponseIdDto(id);
    }

    /**
     * 시간표 삭제
     * <p>해당 이름의 시간표를 삭제합니다.</p>
     *
     * @param tableId 시간표 ID
     * @return 삭제된 시간표 ID
     */
    @DeleteMapping("/{tableId}")
    @UserOnly
    public ResponseIdDto delete(AppAuthentication auth, @PathVariable Long tableId) {
        Long id = timeTableService.delete(auth.getUserId(), tableId);
        return new ResponseIdDto(id);
    }
}
