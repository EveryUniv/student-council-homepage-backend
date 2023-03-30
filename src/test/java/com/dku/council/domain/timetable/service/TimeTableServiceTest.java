package com.dku.council.domain.timetable.service;

import com.dku.council.domain.post.service.DummyPage;
import com.dku.council.domain.timetable.exception.TimeTableNotFoundException;
import com.dku.council.domain.timetable.model.dto.TimePromise;
import com.dku.council.domain.timetable.model.dto.request.CreateTimeTableRequestDto;
import com.dku.council.domain.timetable.model.dto.request.RequestScheduleDto;
import com.dku.council.domain.timetable.model.dto.response.LectureTemplateDto;
import com.dku.council.domain.timetable.model.dto.response.ListTimeTableDto;
import com.dku.council.domain.timetable.model.dto.response.TimeScheduleDto;
import com.dku.council.domain.timetable.model.dto.response.TimeTableDto;
import com.dku.council.domain.timetable.model.entity.LectureTemplate;
import com.dku.council.domain.timetable.model.entity.TimeSchedule;
import com.dku.council.domain.timetable.model.entity.TimeTable;
import com.dku.council.domain.timetable.repository.LectureTemplateRepository;
import com.dku.council.domain.timetable.repository.TimeScheduleRepository;
import com.dku.council.domain.timetable.repository.TimeTableRepository;
import com.dku.council.domain.timetable.repository.spec.LectureTemplateSpec;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.LectureMock;
import com.dku.council.mock.LectureTemplateMock;
import com.dku.council.mock.TimeTableMock;
import com.dku.council.mock.UserMock;
import com.dku.council.util.ObjectMapperGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TimeTableServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TimeTableRepository timeTableRepository;

    @Mock
    private LectureTemplateRepository lectureTemplateRepository;

    @Mock
    private TimeScheduleRepository timeScheduleRepository;

    private TimeTableService service;

    private final ObjectMapper mapper = ObjectMapperGenerator.create();
    private TimeTable table;
    private List<TimeSchedule> schedules;
    private List<TimeScheduleDto> schedulesDto;
    private List<LectureTemplate> lectures;

    @BeforeEach
    void setup() {
        service = new TimeTableService(mapper, userRepository, lectureTemplateRepository, timeTableRepository, timeScheduleRepository);
        table = TimeTableMock.createDummy();

        schedules = LectureMock.createLectureList(10);
        for (TimeSchedule schedule : schedules) {
            schedule.changeTimeTable(table);
        }

        schedulesDto = schedules.stream()
                .map(e -> new TimeScheduleDto(mapper, e))
                .collect(Collectors.toList());

        lectures = LectureTemplateMock.createList(10);
    }


    @Test
    @DisplayName("수업 목록 조회")
    void listLectures() {
        // given
        Specification<LectureTemplate> spec = LectureTemplateSpec.withTitle("");
        Pageable pageable = Pageable.unpaged();
        Page<LectureTemplate> pages = new DummyPage<>(lectures);
        given(lectureTemplateRepository.findAll(spec, pageable)).willReturn(pages);

        // when
        Page<LectureTemplateDto> actual = service.listLectures(spec, pageable);

        // then
        List<LectureTemplateDto> lectureDtos = lectures.stream()
                .map(e -> new LectureTemplateDto(mapper, e))
                .collect(Collectors.toList());
        assertThat(actual).containsExactlyInAnyOrderElementsOf(lectureDtos);
    }

    @Test
    @DisplayName("시간표 목록 조회")
    void list() {
        // given
        User user = UserMock.createDummyMajor();
        List<String> tableNames = List.of("name1", "name2", "name3", "name4");
        List<TimeTable> tables = tableNames.stream()
                .map(name -> new TimeTable(user, name))
                .collect(Collectors.toList());

        given(timeTableRepository.findAllByUserId(user.getId())).willReturn(tables);

        // when
        List<ListTimeTableDto> actual = service.list(user.getId());

        // then
        List<String> actualNames = actual.stream().map(ListTimeTableDto::getName)
                .collect(Collectors.toList());
        assertThat(actualNames).containsExactlyInAnyOrderElementsOf(tableNames);
    }

    @Test
    @DisplayName("시간표 단건 조회")
    void findOne() {
        // given
        given(timeTableRepository.findById(table.getId())).willReturn(Optional.of(table));

        // when
        TimeTableDto actual = service.findOne(table.getUser().getId(), table.getId());

        // then
        assertThat(actual.getId()).isEqualTo(table.getId());
        assertThat(actual.getName()).isEqualTo(table.getName());
        assertThat(actual.getLectures()).containsExactlyInAnyOrderElementsOf(schedulesDto);
    }

    @Test
    @DisplayName("시간표 단건 조회 - 내 시간표가 아닌 경우")
    void failedFindOneByNotMine() {
        // given
        given(timeTableRepository.findById(table.getId())).willReturn(Optional.of(table));

        // when & then
        Assertions.assertThrows(TimeTableNotFoundException.class, () ->
                service.findOne(-1L, table.getId()));
    }

    @Test
    @DisplayName("시간표 생성")
    void create() {
        // given
        List<RequestScheduleDto> reqDtos = schedules.stream()
                .map(e -> new RequestScheduleDto(e.getId(), null, null, List.of(), e.getColor()))
                .collect(Collectors.toList());
        CreateTimeTableRequestDto dto = new CreateTimeTableRequestDto(table.getName(), reqDtos);

        given(userRepository.findById(table.getUser().getId())).willReturn(Optional.of(table.getUser()));
        given(lectureTemplateRepository.findAllById(any())).willReturn(lectures);
        given(timeTableRepository.save(tableCheckArgThat())).willReturn(table);
        given(timeScheduleRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Long id = service.create(table.getUser().getId(), dto);

        // then
        verify(lectureTemplateRepository).findAllById(any());
        assertThat(id).isEqualTo(table.getId());
    }

    @Test
    @DisplayName("시간표 생성 - 일정 추가")
    void createWithSchedule() {
        // given
        List<RequestScheduleDto> reqDtos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            RequestScheduleDto dto = new RequestScheduleDto(null, "name-" + i, "memo-" + i,
                    List.of(
                            new TimePromise(LocalTime.of(9, 0), LocalTime.of(10, 0),
                                    DayOfWeek.MONDAY, "place")
                    ),
                    "color-" + i);
            reqDtos.add(dto);
        }

        table.getSchedules().clear();
        table.getSchedules().addAll(reqDtos.stream()
                .map(e -> TimeSchedule.builder()
                        .name(e.getName())
                        .memo(e.getMemo())
                        .color(e.getColor())
                        .timesJson(TimePromise.serialize(mapper, e.getTimes()))
                        .build())
                .collect(Collectors.toList()));

        CreateTimeTableRequestDto dto = new CreateTimeTableRequestDto(table.getName(), reqDtos);

        given(userRepository.findById(table.getUser().getId())).willReturn(Optional.of(table.getUser()));
        given(lectureTemplateRepository.findAllById(any())).willReturn(List.of());
        given(timeTableRepository.save(tableCheckArgThat())).willReturn(table);
        given(timeScheduleRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Long id = service.create(table.getUser().getId(), dto);

        // then
        verify(lectureTemplateRepository).findAllById(any());
        assertThat(id).isEqualTo(table.getId());
    }

    @Test
    @DisplayName("시간표 수정")
    void update() {
        // given
        List<RequestScheduleDto> reqDtos = schedules.stream()
                .map(e -> new RequestScheduleDto(e.getId(), null, null, List.of(), e.getColor()))
                .collect(Collectors.toList());
        given(timeTableRepository.findById(table.getId())).willReturn(Optional.of(table));
        given(lectureTemplateRepository.findAllById(any())).willReturn(lectures);
        given(timeScheduleRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Long id = service.update(table.getUser().getId(), table.getId(), reqDtos);

        // then
        List<RequestScheduleDto> tableLectures = IntStream.range(0, table.getSchedules().size())
                .mapToObj(i -> {
                    TimeSchedule schedule = table.getSchedules().get(i);
                    return new RequestScheduleDto((long) i, null, null, List.of(), schedule.getColor());
                })
                .collect(Collectors.toList());
        verify(lectureTemplateRepository).findAllById(any());
        assertThat(id).isEqualTo(table.getId());
        assertThat(tableLectures).containsExactlyInAnyOrderElementsOf(reqDtos);
    }

    @Test
    @DisplayName("시간표 수정 - 내 시간표가 아닌 경우")
    void failedUpdateByNotMine() {
        // given
        given(timeTableRepository.findById(table.getId())).willReturn(Optional.of(table));

        // when & then
        Assertions.assertThrows(TimeTableNotFoundException.class, () ->
                service.update(-1L, table.getId(), List.of()));
    }

    @Test
    @DisplayName("시간표 이름 수정")
    void updateName() {
        // given
        given(timeTableRepository.findById(table.getId())).willReturn(Optional.of(table));

        // when
        Long id = service.updateName(table.getUser().getId(), table.getId(), "NewName");

        // then
        assertThat(id).isEqualTo(table.getId());
        assertThat(table.getName()).isEqualTo("NewName");
    }

    @Test
    @DisplayName("시간표 이름 수정 - 내 시간표가 아닌 경우")
    void failedUpdateNameByNotMine() {
        // given
        given(timeTableRepository.findById(table.getId())).willReturn(Optional.of(table));

        // when & then
        Assertions.assertThrows(TimeTableNotFoundException.class, () ->
                service.updateName(-1L, table.getId(), "New Name"));
    }

    @Test
    @DisplayName("시간표 삭제")
    void delete() {
        // given
        given(timeTableRepository.findById(table.getId())).willReturn(Optional.of(table));

        // when
        Long id = service.delete(table.getUser().getId(), table.getId());

        // then
        assertThat(id).isEqualTo(table.getId());
        verify(timeTableRepository).delete(tableCheckArgThat());
    }

    @Test
    @DisplayName("시간표 삭제 - 내 시간표가 아닌 경우")
    void failedDeleteByNotMine() {
        // given
        given(timeTableRepository.findById(table.getId())).willReturn(Optional.of(table));

        // when & then
        Assertions.assertThrows(TimeTableNotFoundException.class, () ->
                service.delete(-1L, table.getId()));
    }

    private TimeTable tableCheckArgThat() {
        return argThat(t -> {
            assertThat(t.getName()).isEqualTo(table.getName());
            for (int i = 0; i < t.getSchedules().size(); i++) {
                TimeSchedule actual = t.getSchedules().get(i);
                TimeSchedule expect = table.getSchedules().get(i);
                assertThat(actual.getName()).isEqualTo(expect.getName());
                assertThat(actual.getMemo()).isEqualTo(expect.getMemo());
                assertThat(actual.getColor()).isEqualTo(expect.getColor());
                assertThat(actual.getTimesJson()).isEqualTo(expect.getTimesJson());
            }
            return true;
        });
    }
}