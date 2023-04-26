package com.dku.council.domain.ticket.controller;

import com.dku.council.domain.ticket.model.dto.response.ResponseManagerTicketDto;
import com.dku.council.domain.ticket.model.dto.response.ResponseTicketDto;
import com.dku.council.domain.ticket.service.TicketVerifyService;
import com.dku.council.mock.user.UserAuth;
import com.dku.council.util.base.AbstractAuthControllerTest;
import com.dku.council.util.test.ImportsForMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketVerifyController.class)
@ImportsForMvc
class TicketVerifyControllerTest extends AbstractAuthControllerTest {

    @MockBean
    private TicketVerifyService ticketVerifyService;

    @BeforeEach
    void setUp() {
        UserAuth.withAdmin(9L);
    }


    @Test
    @DisplayName("내 티켓 가져오기")
    void myTicket() throws Exception {
        // given
        UserAuth.withUser(7L);
        ResponseTicketDto ticket = new ResponseTicketDto(5L, "name",
                "major", "studentId", false, 4);

        when(ticketVerifyService.myTicket(7L, 5L)).thenReturn(ticket);

        // when
        ResultActions actions = mvc.perform(get("/ticket/event/5"))
                .andExpect(status().isOk());

        // then
        actions.andExpect(jsonPath("$.id").value(ticket.getId()))
                .andExpect(jsonPath("$.name").value(ticket.getName()))
                .andExpect(jsonPath("$.major").value(ticket.getMajor()))
                .andExpect(jsonPath("$.studentId").value(ticket.getStudentId()))
                .andExpect(jsonPath("$.issued").value(ticket.isIssued()))
                .andExpect(jsonPath("$.turn").value(ticket.getTurn()));
    }

    @Test
    @DisplayName("티켓 정보 가져오기")
    void getTicketInfo() throws Exception {
        // given
        ResponseTicketDto ticket = new ResponseTicketDto(5L, "name",
                "major", "studentId", false, 4);
        ResponseManagerTicketDto response = new ResponseManagerTicketDto(ticket, "123456");

        when(ticketVerifyService.getTicketInfo(5L)).thenReturn(response);

        // when
        ResultActions actions = mvc.perform(get("/ticket/5"))
                .andExpect(status().isOk());

        // then
        actions.andExpect(jsonPath("$.id").value(ticket.getId()))
                .andExpect(jsonPath("$.name").value(ticket.getName()))
                .andExpect(jsonPath("$.major").value(ticket.getMajor()))
                .andExpect(jsonPath("$.studentId").value(ticket.getStudentId()))
                .andExpect(jsonPath("$.issued").value(ticket.isIssued()))
                .andExpect(jsonPath("$.turn").value(ticket.getTurn()))
                .andExpect(jsonPath("$.code").value(response.getCode()));
    }

    @Test
    @DisplayName("SMS 재전송")
    void resendSms() throws Exception {
        // given
        when(ticketVerifyService.resendSms(5L)).thenReturn("123456");

        // when
        ResultActions actions = mvc.perform(post("/ticket/5/sms")
                        .with(csrf()))
                .andExpect(status().isOk());

        // then
        actions.andExpect(jsonPath("$.code").value("123456"));
    }

    @Test
    @DisplayName("티켓 발급 처리")
    void setToIssued() throws Exception {
        // when
        mvc.perform(post("/ticket/5/permit")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("티켓 발급 취소 처리")
    void setToUnissued() throws Exception {
        // when
        mvc.perform(delete("/ticket/5/permit")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}