package com.dku.council.debug;

import com.dku.council.domain.mainpage.scheduler.ScheduleInfoScheduler;
import com.dku.council.domain.timetable.scheduler.LectureDumpScheduler;
import com.dku.council.global.auth.role.AdminOnly;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자", description = "관리자 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ScheduleInfoScheduler scheduleInfoScheduler;
    private final LectureDumpScheduler lectureDumpScheduler;

    /**
     * 대학교 학사 일정을 업데이트합니다.
     * <p>주기적으로 자동 업데이트되지만, 직접 업데이트할 수 있습니다. 처리에 시간이 좀 걸릴 수 있습니다.</p>
     */
    @PostMapping("/load/schedule")
    @AdminOnly
    public void loadSchedule(){
        scheduleInfoScheduler.loadToDB();
    }

    /**
     * 수업 목록을 업데이트합니다.
     * <p>주기적으로 자동 업데이트되지만, 직접 업데이트할 수 있습니다. 처리에 시간이 좀 걸릴 수 있습니다.</p>
     */
    @PostMapping("/load/lecture")
    @AdminOnly
    public void loadLecture(){
        lectureDumpScheduler.loadToDB();
    }
}
