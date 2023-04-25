package com.dku.council.domain.ticket.controller;

import com.dku.council.domain.ticket.model.dto.TicketEventDto;
import com.dku.council.domain.ticket.model.dto.request.RequestEnrollDto;
import com.dku.council.domain.ticket.model.dto.request.RequestNewTicketEventDto;
import com.dku.council.domain.ticket.model.dto.response.ResponseTicketDto;
import com.dku.council.domain.ticket.service.TicketEventService;
import com.dku.council.domain.ticket.service.TicketService;
import com.dku.council.domain.user.repository.UserInfoMemoryRepository;
import com.dku.council.infra.captcha.model.Captcha;
import com.dku.council.infra.captcha.service.CaptchaService;
import com.dku.council.mock.TicketEventMock;
import com.dku.council.mock.UserInfoMock;
import com.dku.council.mock.user.UserAuth;
import com.dku.council.util.DateStringUtil;
import com.dku.council.util.base.AbstractAuthControllerTest;
import com.dku.council.util.test.ImportsForMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
@ImportsForMvc
class TicketControllerTest extends AbstractAuthControllerTest {

    @MockBean
    private TicketService ticketService;

    @MockBean
    private TicketEventService ticketEventService;

    @MockBean
    private CaptchaService captchaService;

    @MockBean
    private UserInfoMemoryRepository userMemoryRepository;

    @Test
    @DisplayName("티켓 이벤트 목록 가져오기")
    void list() throws Exception {
        // given
        List<TicketEventDto> ticketEventDtos = List.of(
                TicketEventMock.createDummyDto(1L),
                TicketEventMock.createDummyDto(2L),
                TicketEventMock.createDummyDto(3L),
                TicketEventMock.createDummyDto(4L)
        );
        when(ticketEventService.list()).thenReturn(ticketEventDtos);

        // when
        ResultActions actions = mvc.perform(get("/ticket/event"))
                .andExpect(status().isOk());

        // then
        for (int i = 0; i < ticketEventDtos.size(); i++) {
            TicketEventDto dto = ticketEventDtos.get(i);
            String fromString = DateStringUtil.toString(objectMapper, dto.getFrom());
            String toString = DateStringUtil.toString(objectMapper, dto.getTo());
            actions = actions.andExpect(jsonPath("$[" + i + "].id").value(dto.getId()))
                    .andExpect(jsonPath("$[" + i + "].name").value(dto.getName()))
                    .andExpect(jsonPath("$[" + i + "].from").value(fromString))
                    .andExpect(jsonPath("$[" + i + "].to").value(toString));
        }
    }

    @Test
    @DisplayName("티켓 이벤트 생성하기")
    void newTicketEvent() throws Exception {
        // given
        UserAuth.withAdmin(USER_ID);
        RequestNewTicketEventDto dto = new RequestNewTicketEventDto(
                "test",
                LocalDateTime.of(2021, 1, 1, 0, 0),
                LocalDateTime.of(2021, 2, 1, 0, 0),
                1000);

        // when
        ResultActions actions = mvc.perform(post("/ticket/event").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then
        actions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("티켓 이벤트 삭제하기")
    void deleteTicketEvent() throws Exception {
        // given
        UserAuth.withAdmin(USER_ID);

        // when
        ResultActions actions = mvc.perform(delete("/ticket/event/1").with(csrf()));

        // then
        actions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("내 티켓 정보 가져오기")
    void myTicket() throws Exception {
        // given
        ResponseTicketDto dto = new ResponseTicketDto(5);

        when(ticketService.myTicket(USER_ID, 5L)).thenReturn(dto);

        // when
        ResultActions actions = mvc.perform(get("/ticket/5"));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.turn").value(5));
    }

    @Test
    @DisplayName("Captcha 인증 요청")
    void captcha() throws Exception {
        // given
        Captcha captcha = new Captcha("key", "image");
        when(captchaService.requestCaptcha()).thenReturn(captcha);

        // when
        ResultActions actions = mvc.perform(get("/ticket/captcha"));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value(captcha.getKey()))
                .andExpect(jsonPath("$.imageUrl").value(captcha.getImageUrl()));
    }

    @Test
    @DisplayName("티켓 신청하기")
    void enroll() throws Exception {
        // given
        Captcha captcha = new Captcha("key", "image");
        RequestEnrollDto dto = new RequestEnrollDto(5L,
                captcha.getKey(), captcha.getImageUrl());

        when(ticketService.enroll(eq(USER_ID), eq(5L), any()))
                .thenReturn(new ResponseTicketDto(5));
        when(userMemoryRepository.getUserInfo(eq(USER_ID), any()))
                .thenReturn(Optional.of(UserInfoMock.create()));

        // when
        ResultActions actions = mvc.perform(post("/ticket").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.turn").value(5));
        verify(captchaService).verifyCaptcha(captcha.getKey(), captcha.getImageUrl());
    }
}