package com.dku.council.domain.admin.controller;

import com.dku.council.domain.admin.dto.request.RequestCancelPermitDto;
import com.dku.council.domain.batch.TicketScheduler;
import com.dku.council.domain.ticket.service.TicketVerifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/manage/ticket")
@RequiredArgsConstructor
public class TicketPageController {

    private final TicketVerifyService ticketVerifyService;
    private final TicketScheduler ticketScheduler;

    @GetMapping
    public String banner(Model model) {
        model.addAttribute("ticket", new RequestCancelPermitDto(null));
        return "page/ticket/index";
    }

    @PostMapping("/cancelPermit")
    public String cancelPermit(HttpServletRequest request, RequestCancelPermitDto dto) {
        if (dto.getTicketId() != null) {
            ticketVerifyService.setToUnissued(dto.getTicketId());
        }
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/dump")
    public String dumpToDb(HttpServletRequest request) {
        ticketScheduler.dumpToDb();
        return "redirect:" + request.getHeader("Referer");
    }
}
