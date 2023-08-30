package com.dku.council.domain.admin.controller;

import com.dku.council.domain.admin.dto.CancelApprovalTicketsDto;
import com.dku.council.domain.admin.dto.HomeBusPageDto;
import com.dku.council.domain.admin.dto.request.RequestCreateHomeBusDto;
import com.dku.council.domain.admin.service.HomeBusPageService;
import com.dku.council.domain.homebus.model.HomeBusStatus;
import com.dku.council.domain.homebus.model.entity.HomeBus;
import com.dku.council.domain.homebus.model.entity.HomeBusTicket;
import com.dku.council.global.auth.jwt.AppAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manage/home-bus")
public class HomeBusPageController {

    private final HomeBusPageService homeBusPageService;


    @GetMapping
    public String getAllHomeBus(Model model) {
        List<HomeBusPageDto> homeBus = homeBusPageService.getAllHomeBus();
        model.addAttribute("homeBus", homeBus);
        return "page/home-bus/home-buses";
    }

    @PostMapping
    public String addHomeBus(Model model, HttpServletRequest request, RequestCreateHomeBusDto dto) {
        homeBusPageService.create(dto);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/{busId}/delete")
    public String deleteHomeBus(HttpServletRequest request, @PathVariable Long busId) {
        homeBusPageService.delete(busId);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/{busId}/update")
    public String updateHomeBus(HttpServletRequest request, @PathVariable Long busId, @RequestBody RequestCreateHomeBusDto dto) {
        homeBusPageService.update(busId, dto);
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/{busId}")
    public String getBusDetailPage(Model model, @PathVariable Long busId,
                                  @RequestParam(required = false) HomeBusStatus status) {
        HomeBus homeBus = homeBusPageService.getHomeBusById(busId);
        List<HomeBusTicket> tickets = homeBusPageService.getTicketsByBusAndStatus(homeBus, status);
        List<CancelApprovalTicketsDto> cancelApprovalTickets = homeBusPageService.getCancelApprovalTicketByBus(homeBus);
        model.addAttribute("homeBus", homeBus);
        model.addAttribute("tickets", tickets);
        model.addAttribute("cancelApprovalTickets", cancelApprovalTickets);
        return "page/home-bus/home-bus";
    }

    @PostMapping("/ticket/{ticketId}/approval")
    public String updateTicketStatus(HttpServletRequest request, AppAuthentication auth, @PathVariable Long ticketId) {
        Long userId = auth.getUserId();
        homeBusPageService.approvalOrCancleByTicketStatus(ticketId, userId);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/ticket/{ticketId}/cancel")
    public String cancelTicket(HttpServletRequest request, AppAuthentication auth, @PathVariable Long ticketId) {
        Long userId = auth.getUserId();
        homeBusPageService.cancelTicket(ticketId, userId);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/ticket/{ticketId}/cancelNeedApproval")
    public String cancelNeedApprovalTicket(HttpServletRequest request, AppAuthentication auth, @PathVariable Long ticketId) {
        Long userId = auth.getUserId();
        homeBusPageService.cancelNeedApprovalTicket(ticketId);
        return "redirect:" + request.getHeader("Referer");
    }
}
