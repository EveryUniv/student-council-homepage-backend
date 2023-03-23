package com.dku.council.domain.timetable.controller;

import com.dku.council.domain.timetable.model.dto.LectureDto;
import com.dku.council.domain.timetable.model.dto.LectureTimeDto;
import com.dku.council.domain.timetable.model.dto.TimeTableRequestDto;
import com.dku.council.domain.timetable.service.TimeTableService;
import com.dku.council.util.base.AbstractAuthControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TimeTableController.class)
class TimeTableControllerTest extends AbstractAuthControllerTest {

    @MockBean
    private TimeTableService timeTableService;

    @Autowired
    private ObjectMapper objectMapper;

    private final LocalTime start = LocalTime.of(10, 0);
    private final LocalTime end = LocalTime.of(13, 0);
    private List<LectureDto> testLectures;

    @BeforeEach
    void setup() {
        testLectures = List.of(
                new LectureDto("name", "professor", "place", List.of(
                        new LectureTimeDto(start, end, DayOfWeek.MONDAY),
                        new LectureTimeDto(start, end, DayOfWeek.THURSDAY)
                )),
                new LectureDto("name2", "professor2", "place2", List.of(
                        new LectureTimeDto(start, end, DayOfWeek.MONDAY)
                ))
        );
    }

    @Test
    @DisplayName("내 시간표 조회")
    void list() throws Exception {
        // given
        given(timeTableService.list(1L, "test")).willReturn(testLectures);

        // when & then
        mvc.perform(get("/timetable")
                        .param("name", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].professor").value("professor"))
                .andExpect(jsonPath("$[0].place").value("place"))
                .andExpect(jsonPath("$[0].times[0].start").value("10:00:00"))
                .andExpect(jsonPath("$[0].times[0].end").value("13:00:00"))
                .andExpect(jsonPath("$[0].times[0].week").value(DayOfWeek.MONDAY.name()))
                .andExpect(jsonPath("$[0].times[1].start").value("10:00:00"))
                .andExpect(jsonPath("$[0].times[1].end").value("13:00:00"))
                .andExpect(jsonPath("$[0].times[1].week").value(DayOfWeek.THURSDAY.name()))
                .andExpect(jsonPath("$[1].name").value("name2"))
                .andExpect(jsonPath("$[1].professor").value("professor2"))
                .andExpect(jsonPath("$[1].place").value("place2"))
                .andExpect(jsonPath("$[1].times[0].start").value("10:00:00"))
                .andExpect(jsonPath("$[1].times[0].end").value("13:00:00"))
                .andExpect(jsonPath("$[1].times[0].week").value(DayOfWeek.MONDAY.name()));
    }

    @Test
    @DisplayName("시간표 생성")
    void create() throws Exception {
        // given
        TimeTableRequestDto dto = new TimeTableRequestDto("test", testLectures);
        given(timeTableService.create(any(), any())).willReturn(3L);

        // when
        mvc.perform(post("/timetable")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L));
    }

    @Test
    @DisplayName("시간표 수정")
    void update() throws Exception {
        // given
        TimeTableRequestDto dto = new TimeTableRequestDto("test", testLectures);
        given(timeTableService.update(any(), any())).willReturn(3L);

        // when
        mvc.perform(patch("/timetable")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L));
    }

    @Test
    @DisplayName("시간표 삭제")
    void del() throws Exception {
        // given
        given(timeTableService.delete(any(), eq("test"))).willReturn(3L);

        // when
        mvc.perform(delete("/timetable")
                        .with(csrf())
                        .param("name", "test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L));
    }
}