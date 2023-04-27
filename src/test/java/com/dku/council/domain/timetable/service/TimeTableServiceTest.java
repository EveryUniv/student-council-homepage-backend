package com.dku.council.domain.timetable.service;

import com.dku.council.domain.post.service.DummyPage;
import com.dku.council.domain.timetable.exception.TimeConflictException;
import com.dku.council.domain.timetable.exception.TimeTableNotFoundException;
import com.dku.council.domain.timetable.exception.TooSmallTimeException;
import com.dku.council.domain.timetable.model.TimeScheduleType;
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
import com.dku.council.mock.TimeTableMock;
import com.dku.council.mock.UserMock;
import com.dku.council.util.ObjectMapperGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private TimeTable overlappedTable;
    private List<TimeSchedule> schedules;
    private List<TimeSchedule> overlappedSchedules;

    @BeforeEach
    void setup() {
        service = new TimeTableService(mapper, userRepository, lectureTemplateRepository, timeTableRepository, timeScheduleRepository);
        table = TimeTableMock.createDummy();

        schedules = LectureMock.createLectureList();
        for (TimeSchedule schedule : schedules) {
            schedule.changeTimeTable(table);
        }

        overlappedTable = TimeTableMock.createDummy();
        overlappedSchedules = LectureMock.createOverlappedLectureList();
        for (TimeSchedule schedule : overlappedSchedules) {
            schedule.changeTimeTable(overlappedTable);
        }
    }


    @Test
    @DisplayName("수업 목록 조회")
    void listLectures() {
        // given
        List<LectureTemplate> lectures = LectureMock.createLectureTemplateList();
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
        List<TimeScheduleDto> schedulesDto = schedules.stream()
                .map(e -> new TimeScheduleDto(mapper, e))
                .collect(Collectors.toList());
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
                .map(this::scheduleDtoMapper)
                .collect(Collectors.toList());
        CreateTimeTableRequestDto dto = new CreateTimeTableRequestDto(table.getName(), reqDtos);

        given(userRepository.findById(table.getUser().getId())).willReturn(Optional.of(table.getUser()));
        given(timeTableRepository.save(tableCheckArgThat())).willAnswer(invocation -> invocation.getArgument(0));
        given(timeScheduleRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Long id = service.create(table.getUser().getId(), dto);

        // then
        List<RequestScheduleDto> tableLectures = table.getSchedules().stream()
                .map(this::scheduleDtoMapper)
                .collect(Collectors.toList());
        assertThat(id).isEqualTo(table.getId());
        assertThat(tableLectures).containsExactlyInAnyOrderElementsOf(reqDtos);
    }

    @NotNull
    private RequestScheduleDto scheduleDtoMapper(TimeSchedule e) {
        List<TimePromise> timeList = TimePromise.parse(mapper, e.getTimesJson());
        return new RequestScheduleDto(e.getName(), e.getMemo(), e.getType(), timeList, e.getColor());
    }

    @Test
    @DisplayName("시간표 생성 실패 - 겹치는 일정")
    void failedCreateOverlapped() {
        // given
        List<RequestScheduleDto> reqDtos = overlappedSchedules.stream()
                .map(this::scheduleDtoMapper)
                .collect(Collectors.toList());
        CreateTimeTableRequestDto dto = new CreateTimeTableRequestDto(overlappedTable.getName(), reqDtos);

        given(userRepository.findById(overlappedTable.getUser().getId())).willReturn(Optional.of(overlappedTable.getUser()));
        given(timeScheduleRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        // when & then
        Assertions.assertThrows(TimeConflictException.class, () ->
                service.create(overlappedTable.getUser().getId(), dto));
    }

    @Test
    @DisplayName("시간표 생성 실패 - 너무 작은 범위")
    void failedCreateTooSmall() {
        // given
        LocalTime start = LocalTime.of(10, 0, 0);
        List<RequestScheduleDto> reqDtos = List.of(
                new RequestScheduleDto("name", "memo", TimeScheduleType.SCHEDULE, List.of(
                        new TimePromise(
                                start, start.plus(TimeTableService.MINIMUM_DURATION).minusSeconds(1),
                                DayOfWeek.FRIDAY, "place"
                        ),
                        new TimePromise(
                                start, start.minusSeconds(10),
                                DayOfWeek.FRIDAY, "place"
                        )
                ), "#000000")
        );
        CreateTimeTableRequestDto dto = new CreateTimeTableRequestDto(table.getName(), reqDtos);

        given(userRepository.findById(table.getUser().getId())).willReturn(Optional.of(table.getUser()));

        // when & then
        Assertions.assertThrows(TooSmallTimeException.class, () ->
                service.create(table.getUser().getId(), dto));
    }

    @Test
    @DisplayName("시간표 생성 - 일정 추가")
    void createWithSchedule() {
        // given
        CreateTimeTableRequestDto dto = new CreateTimeTableRequestDto(overlappedTable.getName(), List.of());

        given(userRepository.findById(overlappedTable.getUser().getId())).willReturn(Optional.of(overlappedTable.getUser()));
        given(timeTableRepository.save(tableCheckArgThat())).willReturn(overlappedTable);

        // when
        Long id = service.create(overlappedTable.getUser().getId(), dto);

        // then
        assertThat(id).isEqualTo(overlappedTable.getId());
    }

    @Test
    @DisplayName("시간표 수정")
    void update() {
        // given
        List<RequestScheduleDto> reqDtos = schedules.stream()
                .map(this::scheduleDtoMapper)
                .collect(Collectors.toList());
        given(timeTableRepository.findById(table.getId())).willReturn(Optional.of(table));
        given(timeScheduleRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Long id = service.update(table.getUser().getId(), table.getId(), reqDtos);

        // then
        List<RequestScheduleDto> tableLectures = table.getSchedules().stream()
                .map(this::scheduleDtoMapper)
                .collect(Collectors.toList());
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
                assertThat(actual.getType()).isEqualTo(expect.getType());

                List<TimePromise> promise1 = TimePromise.parse(mapper, actual.getTimesJson());
                List<TimePromise> promise2 = TimePromise.parse(mapper, expect.getTimesJson());
                assertThat(promise1).containsExactlyInAnyOrderElementsOf(promise2);
            }
            return true;
        });
    }
}