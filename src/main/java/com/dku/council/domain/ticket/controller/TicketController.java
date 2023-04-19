package com.dku.council.domain.ticket.controller;

import com.dku.council.domain.ticket.model.dto.CaptchaDto;
import com.dku.council.domain.ticket.model.dto.TicketDto;
import com.dku.council.domain.ticket.model.dto.TicketEventDto;
import com.dku.council.global.auth.role.UserAuth;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "티켓", description = "티켓 관련 API")
@RestController
@RequestMapping("/ticket")
public class TicketController {

    /**
     * 티켓 이벤트 목록 가져오기
     * <p>등록된 티켓 이벤트 목록을 가져옵니다.</p>
     *
     * @return 티켓 이벤트 목록
     */
    @GetMapping
    @UserAuth
    public List<TicketEventDto> list() {
        return List.of();
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
    public TicketDto myTicket(@PathVariable Long ticketEventId) {
        return null;
    }

    /**
     * captcha 이미지 가져오기
     * <p>매크로 방지를 위한 Captcha 이미지를 가져옵니다.</p>
     *
     * @return Captcha 이미지
     */
    @GetMapping("/captcha")
    @UserAuth
    public CaptchaDto captcha() {
        return new CaptchaDto();
    }

    /**
     * 티켓 신청하기
     * <p>티켓 이벤트에 신청합니다.</p>
     */
    @PostMapping
    @UserAuth
    public TicketDto enroll(@RequestBody String captcha) {
        return null;
    }
}
