package com.dku.council.debug.controller;

import com.dku.council.domain.batch.LectureDumpScheduler;
import com.dku.council.domain.batch.ScheduleInfoScheduler;
import com.dku.council.domain.batch.TicketScheduler;
import com.dku.council.global.auth.role.AdminAuth;
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
    private final TicketScheduler ticketScheduler;

    /**
     * 대학교 학사 일정을 업데이트합니다.
     * <p>주기적으로 자동 업데이트되지만, 직접 업데이트할 수 있습니다. 처리에 시간이 좀 걸릴 수 있습니다.</p>
     */
    @PostMapping("/load/schedule")
    @AdminAuth
    public void loadSchedule() {
        scheduleInfoScheduler.loadToDB();
    }

    /**
     * 수업 목록을 업데이트합니다.
     * <p>주기적으로 자동 업데이트되지만, 직접 업데이트할 수 있습니다. 처리에 시간이 좀 걸릴 수 있습니다.</p>
     */
    @PostMapping("/load/lecture")
    @AdminAuth
    public void loadLecture() {
        lectureDumpScheduler.loadToDB();
    }

    /**
     * 티켓 목록을 DB에 반영합니다.
     * <p>주기적으로 자동 업데이트되지만, 직접 dump할 수 있습니다.</p>
     */
    @PostMapping("/dump/ticket")
    @AdminAuth
    public void dumpTickets() {
        ticketScheduler.dumpToDb();
    }
}
