package com.dku.council.domain.timetable.controller;

import com.dku.council.domain.post.service.DummyPage;
import com.dku.council.domain.timetable.model.dto.request.CreateTimeTableRequestDto;
import com.dku.council.domain.timetable.model.dto.request.RequestLectureDto;
import com.dku.council.domain.timetable.model.dto.request.UpdateTimeTableNameRequestDto;
import com.dku.council.domain.timetable.model.dto.request.UpdateTimeTableRequestDto;
import com.dku.council.domain.timetable.model.dto.response.*;
import com.dku.council.domain.timetable.service.TimeTableService;
import com.dku.council.util.base.AbstractAuthControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private List<TimeTableLectureDto> testLectureMappings;
    private List<RequestLectureDto> requestLectureDto;

    @BeforeEach
    void setup() {
        testLectures = List.of(
                new LectureDto(1L, "name1", "professor1", List.of(
                        new LectureTimeDto(start, end, DayOfWeek.MONDAY, "place1"),
                        new LectureTimeDto(start, end, DayOfWeek.THURSDAY, "place2")
                )),
                new LectureDto(2L, "name2", "professor2", List.of(
                        new LectureTimeDto(start, end, DayOfWeek.MONDAY, "place1")
                ))
        );

        testLectureMappings = List.of(
                new TimeTableLectureDto("name", "professor", "ffffff",
                        List.of(
                                new LectureTimeDto(start, end, DayOfWeek.MONDAY, "place1"),
                                new LectureTimeDto(start, end, DayOfWeek.THURSDAY, "place2")
                        )),
                new TimeTableLectureDto("name2", "professor2", "aaaaaa",
                        List.of(
                                new LectureTimeDto(start, end, DayOfWeek.MONDAY, "place")
                        ))
        );

        requestLectureDto = List.of(
                new RequestLectureDto(1L, "color1"),
                new RequestLectureDto(2L, "color2"),
                new RequestLectureDto(3L, "color3")
        );
    }



    @Test
    @DisplayName("수업 목록")
    void listLectures() throws Exception {
        // given
        Page<LectureDto> lectures = new DummyPage<>(testLectures);
        given(timeTableService.listLectures(any(), any())).willReturn(lectures);

        // when
        mvc.perform(get("/timetable/lecture"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("name1"))
                .andExpect(jsonPath("$.content[0].professor").value("professor1"))
                .andExpect(jsonPath("$.content[0].times[0].start").value("10:00:00"))
                .andExpect(jsonPath("$.content[0].times[0].end").value("13:00:00"))
                .andExpect(jsonPath("$.content[0].times[0].week").value(DayOfWeek.MONDAY.name()))
                .andExpect(jsonPath("$.content[0].times[0].place").value("place1"))
                .andExpect(jsonPath("$.content[0].times[1].start").value("10:00:00"))
                .andExpect(jsonPath("$.content[0].times[1].end").value("13:00:00"))
                .andExpect(jsonPath("$.content[0].times[1].week").value(DayOfWeek.THURSDAY.name()))
                .andExpect(jsonPath("$.content[0].times[1].place").value("place2"))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].name").value("name2"))
                .andExpect(jsonPath("$.content[1].professor").value("professor2"))
                .andExpect(jsonPath("$.content[1].times[0].start").value("10:00:00"))
                .andExpect(jsonPath("$.content[1].times[0].end").value("13:00:00"))
                .andExpect(jsonPath("$.content[1].times[0].week").value(DayOfWeek.MONDAY.name()))
                .andExpect(jsonPath("$.content[1].times[0].place").value("place1"));

    }

    @Test
    @DisplayName("시간표 목록")
    void list() throws Exception {
        // given
        List<TimeTableInfoDto> tables = List.of(
                new TimeTableInfoDto(1L, "name"),
                new TimeTableInfoDto(2L, "name2")
        );
        given(timeTableService.list(USER_ID)).willReturn(tables);

        // when
        mvc.perform(get("/timetable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("name2"));
    }

    @Test
    @DisplayName("내 시간표 단건 조회")
    void findOne() throws Exception {
        // given
        TimeTableDto dto = new TimeTableDto(3L, "name", testLectureMappings);
        given(timeTableService.findOne(USER_ID, 3L)).willReturn(dto);

        // when & then
        mvc.perform(get("/timetable/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.lectures[0].name").value("name"))
                .andExpect(jsonPath("$.lectures[0].professor").value("professor"))
                .andExpect(jsonPath("$.lectures[0].color").value("ffffff"))
                .andExpect(jsonPath("$.lectures[0].times[0].start").value("10:00:00"))
                .andExpect(jsonPath("$.lectures[0].times[0].end").value("13:00:00"))
                .andExpect(jsonPath("$.lectures[0].times[0].week").value(DayOfWeek.MONDAY.name()))
                .andExpect(jsonPath("$.lectures[0].times[0].place").value("place1"))
                .andExpect(jsonPath("$.lectures[0].times[1].start").value("10:00:00"))
                .andExpect(jsonPath("$.lectures[0].times[1].end").value("13:00:00"))
                .andExpect(jsonPath("$.lectures[0].times[1].week").value(DayOfWeek.THURSDAY.name()))
                .andExpect(jsonPath("$.lectures[0].times[1].place").value("place2"))
                .andExpect(jsonPath("$.lectures[1].name").value("name2"))
                .andExpect(jsonPath("$.lectures[1].professor").value("professor2"))
                .andExpect(jsonPath("$.lectures[1].color").value("aaaaaa"))
                .andExpect(jsonPath("$.lectures[1].times[0].start").value("10:00:00"))
                .andExpect(jsonPath("$.lectures[1].times[0].end").value("13:00:00"))
                .andExpect(jsonPath("$.lectures[1].times[0].week").value(DayOfWeek.MONDAY.name()))
                .andExpect(jsonPath("$.lectures[1].times[0].place").value("place"));
    }

    @Test
    @DisplayName("시간표 생성")
    void create() throws Exception {
        // given
        CreateTimeTableRequestDto dto = new CreateTimeTableRequestDto("test", requestLectureDto);
        given(timeTableService.create(eq(USER_ID), any())).willReturn(3L);

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
        UpdateTimeTableRequestDto dto = new UpdateTimeTableRequestDto(requestLectureDto);
        given(timeTableService.update(eq(USER_ID), eq(3L), any())).willReturn(3L);

        // when
        mvc.perform(patch("/timetable/3")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L));
    }

    @Test
    @DisplayName("시간표 이름 수정")
    void updateName() throws Exception {
        // given
        UpdateTimeTableNameRequestDto dto = new UpdateTimeTableNameRequestDto("newName");
        given(timeTableService.updateName(USER_ID, 3L, "newName")).willReturn(3L);

        // when
        mvc.perform(patch("/timetable/name/3")
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
        given(timeTableService.delete(USER_ID, 3L)).willReturn(3L);

        // when
        mvc.perform(delete("/timetable/3").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L));
    }
}