package com.dku.council.domain.ticket;

import com.dku.council.domain.ticket.model.dto.request.RequestEnrollDto;
import com.dku.council.domain.ticket.model.dto.request.RequestNewTicketEventDto;
import com.dku.council.domain.ticket.model.entity.TicketEvent;
import com.dku.council.domain.ticket.repository.TicketEventRepository;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.model.dto.ResponseIdDto;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.UserMock;
import com.dku.council.mock.user.UserAuth;
import com.dku.council.util.DateStringUtil;
import com.dku.council.util.MvcMockResponse;
import com.dku.council.util.base.AbstractContainerRedisTest;
import com.dku.council.util.test.FullIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@FullIntegrationTest
public class TicketIntegrationTest extends AbstractContainerRedisTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketEventRepository ticketEventRepository;

    private User user;

    @BeforeEach
    void setupUser() {
        Major major = majorRepository.save(MajorMock.create());

        user = UserMock.create(0L, major);
        user = userRepository.save(user);
        UserAuth.withUser(user.getId());
    }

    @Test
    @DisplayName("티켓 이벤트 목록 CRD")
    public void eventCRD() throws Exception {
        // given
        UserAuth.withAdmin(user.getId());
        RequestNewTicketEventDto dto1 = new RequestNewTicketEventDto("name",
                LocalDateTime.of(2021, 1, 1, 0, 0),
                LocalDateTime.of(2021, 2, 1, 0, 0),
                1000);
        RequestNewTicketEventDto dto2 = new RequestNewTicketEventDto("name2",
                LocalDateTime.of(2022, 1, 1, 0, 0),
                LocalDateTime.of(2022, 2, 1, 0, 0),
                500);

        // when
        MvcResult addResult = mvc.perform(post("/ticket/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto1)))
                .andExpect(status().isOk())
                .andReturn();

        ResultActions get1 = mvc.perform(get("/ticket/event"));

        mvc.perform(post("/ticket/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto2)))
                .andExpect(status().isOk());

        ResponseIdDto addResponse = MvcMockResponse.getResponse(objectMapper, addResult, ResponseIdDto.class);
        mvc.perform(delete("/ticket/event/" + addResponse.getId()))
                .andExpect(status().isOk());

        ResultActions get2 = mvc.perform(get("/ticket/event"));

        // then
        String dto1StartAt = DateStringUtil.toString(objectMapper, dto1.getStartAt());
        String dto1EndAt = DateStringUtil.toString(objectMapper, dto1.getEndAt());
        get1.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(dto1.getName()))
                .andExpect(jsonPath("$[0].from").value(dto1StartAt))
                .andExpect(jsonPath("$[0].to").value(dto1EndAt));

        String dto2StartAt = DateStringUtil.toString(objectMapper, dto2.getStartAt());
        String dto2EndAt = DateStringUtil.toString(objectMapper, dto2.getEndAt());
        get2.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(dto2.getName()))
                .andExpect(jsonPath("$[0].from").value(dto2StartAt))
                .andExpect(jsonPath("$[0].to").value(dto2EndAt));
    }

    @Test
    @DisplayName("티켓팅")
    public void enroll() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();

        TicketEvent event = new TicketEvent("name", now.minusSeconds(1), now.plusHours(1), 1000);
        event = ticketEventRepository.save(event);
        RequestEnrollDto dto = new RequestEnrollDto(event.getId(), "key", "value");

        TicketEvent event2 = new TicketEvent("name2", now.plusSeconds(1), now.plusHours(1), 1000);
        event2 = ticketEventRepository.save(event2);
        RequestEnrollDto dto2 = new RequestEnrollDto(event2.getId(), "key", "value");

        // when & then
        mvc.perform(get("/ticket/reservation/" + event.getId()))
                .andExpect(status().isNotFound());

        mvc.perform(post("/ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.turn").value(1));

        mvc.perform(post("/ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto2)))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/ticket")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/ticket/reservation/" + event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.turn").value(1));
    }
}
