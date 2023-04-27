package com.dku.council.domain.ticket.controller;

import com.dku.council.domain.ticket.model.dto.response.ResponseManagerTicketDto;
import com.dku.council.domain.ticket.model.dto.response.ResponseResendSmsDto;
import com.dku.council.domain.ticket.model.dto.response.ResponseTicketDto;
import com.dku.council.domain.ticket.service.TicketVerifyService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.AdminAuth;
import com.dku.council.global.auth.role.UserAuth;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "티켓 검증", description = "티켓 검증 관련 API")
@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class TicketVerifyController {

    private final TicketVerifyService ticketVerifyService;

    /**
     * 내 티켓 조회하기
     * <p>실제로 발급받은 내 티켓을 조회합니다. 만약 신청만하고 대상자로 선정되지 않은 경우에는 티켓이 없습니다.</p>
     *
     * @param eventId 티켓 이벤트 아이디
     */
    @GetMapping("/event/{eventId}")
    @UserAuth
    public ResponseTicketDto myTicket(AppAuthentication auth, @PathVariable Long eventId) {
        return ticketVerifyService.myTicket(auth.getUserId(), eventId);
    }

    /**
     * 티켓 정보 가져오기 (Admin)
     * <p>티켓 ID로 상세한 정보를 가져옵니다. 발급자 이름, 학과, 학번, 발급 여부, SMS 인증 번호를 가져옵니다.</p>
     * <p>이 API를 호출하면 즉시 SMS도 함께 전송됩니다.</p>
     *
     * @param ticketId 티켓 아이디
     */
    @GetMapping("/{ticketId}")
    @AdminAuth
    public ResponseManagerTicketDto getTicketInfo(@PathVariable Long ticketId) {
        return ticketVerifyService.getTicketInfo(ticketId);
    }

    /**
     * 티켓 본인 인증용 SMS 재전송 (Admin)
     * <p>티켓 정보를 조회하면 자동으로 전송되지만, 문제가 발생하면 이 API를 사용해 직접 재전송할 수도 있습니다.</p>
     *
     * @param ticketId 티켓 아이디
     */
    @PostMapping("/{ticketId}/sms")
    @AdminAuth
    public ResponseResendSmsDto resendSms(@PathVariable Long ticketId) {
        String code = ticketVerifyService.resendSms(ticketId);
        return new ResponseResendSmsDto(code);
    }

    /**
     * 티켓 발급처리하기 (Admin)
     * <p>티켓이 발급처리합니다. 중복 발급을 막고, 입장시 확인을 위해 실물 티켓을 발급했다는 의미로 사용합니다.</p>
     *
     * @param ticketId 티켓 아이디
     */
    @PostMapping("/{ticketId}/permit")
    @AdminAuth
    public void setToIssued(@PathVariable Long ticketId) {
        ticketVerifyService.setToIssued(ticketId);
    }

    /**
     * 티켓 발급 취소하기 (Admin)
     * <p>발급 처리된 티켓을 다시 미발급으로 바꿉니다. 실수나 정책 변경 사항으로 인해 발급한 경우 다시 취소할 수 있습니다.</p>
     *
     * @param ticketId 티켓 아이디
     */
    @DeleteMapping("/{ticketId}/permit")
    @AdminAuth
    public void setToUnissued(@PathVariable Long ticketId) {
        ticketVerifyService.setToUnissued(ticketId);
    }
}
