package com.dku.council.domain.homebus.controller;

import com.dku.council.domain.homebus.model.dto.HomeBusDto;
import com.dku.council.domain.homebus.model.dto.RequestCancelTicketDto;
import com.dku.council.domain.homebus.service.HomeBusUserService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.UserAuth;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "귀향버스", description = "귀향버스 api")
@RestController
@RequestMapping("/homebus")
@RequiredArgsConstructor
public class HomeBusUserController {

    private final HomeBusUserService service;

    /**
     * 귀향버스 목록 조회.
     * <p>등록된 모든 귀향 버스 목록을 조회합니다. 버스 호차, 경로, 종착지, 잔여석, 상태 목록을 반환합니다.</p>
     *
     * @return 귀향버스 목록
     */
    @GetMapping
    @UserAuth
    public List<HomeBusDto> listBus(AppAuthentication auth) {
        return service.listBus(auth.getUserId());
    }

    /**
     * 승차권 신청.
     * <p>승차권을 신청합니다. 승인받기 전까지는 사용할 수 없습니다.</p>
     */
    @PostMapping("/ticket/{busId}")
    @UserAuth
    public void createTicket(AppAuthentication auth, @PathVariable Long busId) {
        service.createTicket(auth.getUserId(), busId);
    }

    /**
     * 승차권 취소 요청.
     * <p>승인받은 승차권에 대해 취소 요청합니다. 승인 대기중인 승차권은 취소 대상이 아닙니다.</p>
     * <p>취소 요청시 예금주명, 입금 받을 계좌, 은행을 기입해야합니다.</p>
     */
    @DeleteMapping("/ticket/{busId}")
    @UserAuth
    public void deleteTicket(AppAuthentication auth, @PathVariable Long busId,
                             @Valid @RequestBody RequestCancelTicketDto dto) {
        service.deleteTicket(auth.getUserId(), busId, dto);
    }
}
