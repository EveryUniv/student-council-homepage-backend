package com.dku.council.domain.ticket.controller;

import com.dku.council.domain.ticket.model.dto.*;
import com.dku.council.domain.ticket.service.TicketService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.AdminAuth;
import com.dku.council.global.auth.role.UserAuth;
import com.dku.council.infra.captcha.model.Captcha;
import com.dku.council.infra.captcha.service.CaptchaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "티켓", description = "티켓 관련 API")
@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final CaptchaService captchaService;

    /**
     * 티켓 이벤트 목록 가져오기
     * <p>등록된 티켓 이벤트 목록을 가져옵니다.</p>
     *
     * @return 티켓 이벤트 목록
     */
    @GetMapping("/event")
    public List<TicketEventDto> list() {
        return ticketService.list();
    }

    /**
     * 티켓 이벤트 등록하기
     *
     * @param dto 티켓 이벤트 정보
     */
    @PostMapping("/event")
    @AdminAuth
    public void newTicketEvent(@Valid @RequestBody RequestNewTicketEventDto dto) {
        ticketService.newTicketEvent(dto);
    }

    /**
     * 티켓 이벤트 삭제하기
     * <p>티켓 이벤트를 삭제합니다. 이벤트에 등록된 사용자들의 티켓도 함께 삭제됩니다.</p>
     *
     * @param ticketEventId 티켓 이벤트 아이디
     */
    @DeleteMapping("/event/{ticketEventId}")
    @AdminAuth
    public void deleteTicketEvent(@PathVariable Long ticketEventId) {
        ticketService.deleteTicketEvent(ticketEventId);
    }

    /**
     * 내 티켓 보기
     * <p>내가 신청한 특정 이벤트의 티켓을 보여줍니다. 예비 번호가 포함되어있습니다.</p>
     *
     * @param ticketEventId 티켓 이벤트 아이디
     * @return 티켓 정보
     */
    @GetMapping("/{ticketEventId}")
    @UserAuth
    public TicketDto myTicket(AppAuthentication auth, @PathVariable Long ticketEventId) {
        return ticketService.myTicket(auth.getUserId(), ticketEventId);
    }

    /**
     * captcha 인증 요청
     * <p>매크로 방지를 위한 Captcha 이미지를 요청합니다.</p>
     *
     * @return Captcha 이미지
     */
    @GetMapping("/captcha")
    public CaptchaDto captcha() {
        Captcha captcha = captchaService.requestCaptcha();
        return new CaptchaDto(captcha);
    }

    /**
     * 티켓 신청하기
     * <p>티켓 이벤트에 신청합니다.</p>
     *
     * @param dto 티켓 신청 정보
     */
    @PostMapping
    @UserAuth
    public TicketDto enroll(AppAuthentication auth,
                            @Valid @RequestBody RequestEnrollDto dto) {
        captchaService.verifyCaptcha(dto.getCaptchaKey(), dto.getCaptchaValue());
        return ticketService.enroll(auth.getUserId(), dto.getEventId());
    }
}