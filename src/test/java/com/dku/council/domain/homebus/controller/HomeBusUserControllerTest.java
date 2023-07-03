package com.dku.council.domain.homebus.controller;

import com.dku.council.domain.homebus.model.dto.HomeBusDto;
import com.dku.council.domain.homebus.model.dto.RequestCancelTicketDto;
import com.dku.council.domain.homebus.service.HomeBusUserService;
import com.dku.council.mock.HomeBusMock;
import com.dku.council.mock.user.UserAuth;
import com.dku.council.util.base.AbstractAuthControllerTest;
import com.dku.council.util.test.ImportsForMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(HomeBusUserController.class)
@ImportsForMvc
class HomeBusUserControllerTest extends AbstractAuthControllerTest {

    @MockBean
    private HomeBusUserService homeBusUserService;

    @Test
    @DisplayName("귀향버스 목록 조회")
    void listBus() throws Exception{
        //given
        List<HomeBusDto> homeBusDtos = List.of(
                HomeBusMock.createDummyDto(1L),
                HomeBusMock.createDummyDto(2L),
                HomeBusMock.createDummyDto(3L)
        );
        when(homeBusUserService.listBus()).thenReturn(homeBusDtos);

        //when
        ResultActions actions = mvc.perform(get("/homebus"))
                .andExpect(status().isOk());

        //then
        for (int i = 0; i < homeBusDtos.size(); i++) {
            HomeBusDto dto = homeBusDtos.get(i);
            actions.andExpect(jsonPath(String.format("$[%d].label", i)).value(dto.getLabel()))
                    .andExpect(jsonPath(String.format("$[%d].path", i)).value(dto.getPath()))
                    .andExpect(jsonPath(String.format("$[%d].destination", i)).value(dto.getDestination()))
                    .andExpect(jsonPath(String.format("$[%d].totalSeats", i)).value(dto.getTotalSeats()))
                    .andExpect(jsonPath(String.format("$[%d].remainingSeats", i)).value(dto.getRemainingSeats()));
        }
    }

    @Test
    @DisplayName("귀향버스 버스 티켓 신청")
    void createTicket() throws Exception {
        //given
        UserAuth.withUser(USER_ID);
        HomeBusDto dto = HomeBusMock.createDummyDto(5L);

        //when
        ResultActions actions = mvc.perform(post("/homebus/ticket/5").with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        actions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("귀향버스 버스 티켓 취소 요청")
    void deleteTicket() throws Exception{
        //given
        UserAuth.withUser(USER_ID);
        RequestCancelTicketDto dto = new RequestCancelTicketDto(
                "depositor", "accountNum", "bankName");

        //when
        ResultActions actions = mvc.perform(delete("/homebus/ticket/5").with(csrf())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        //then
        actions.andExpect(status().isOk());
    }
}