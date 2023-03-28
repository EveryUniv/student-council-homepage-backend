package com.dku.council.domain.timetable.service;

import com.dku.council.domain.timetable.exception.TimeTableNotFoundException;
import com.dku.council.domain.timetable.model.dto.request.CreateTimeTableRequestDto;
import com.dku.council.domain.timetable.model.dto.request.RequestLectureDto;
import com.dku.council.domain.timetable.model.dto.response.LectureDto;
import com.dku.council.domain.timetable.model.dto.response.TimeTableDto;
import com.dku.council.domain.timetable.model.dto.response.TimeTableInfoDto;
import com.dku.council.domain.timetable.model.entity.Lecture;
import com.dku.council.domain.timetable.model.entity.TimeTable;
import com.dku.council.domain.timetable.model.entity.TimeTableLecture;
import com.dku.council.domain.timetable.repository.LectureRepository;
import com.dku.council.domain.timetable.repository.TimeTableRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.LectureMock;
import com.dku.council.mock.TimeTableMock;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private LectureRepository lectureRepository;

    @InjectMocks
    private TimeTableService service;

    private TimeTable table;
    private List<Lecture> lectures;
    private List<TimeTableLecture> lectureMappings;
    private List<LectureDto> lectureDtos;

    @BeforeEach
    void setup() {
        table = TimeTableMock.createDummy();
        lectures = LectureMock.createLectureList(10);

        lectureMappings = lectures.stream()
                .map(lecture -> new TimeTableLecture(lecture, "color" + lecture.getName()))
                .collect(Collectors.toList());
        lectureMappings.forEach(lecture -> lecture.changeTimeTable(table));
        lectureDtos = lectureMappings.stream()
                .map(LectureDto::new)
                .collect(Collectors.toList());
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
        List<TimeTableInfoDto> actual = service.list(user.getId());

        // then
        List<String> actualNames = actual.stream().map(TimeTableInfoDto::getName)
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
        assertThat(actual.getLectures()).containsExactlyInAnyOrderElementsOf(lectureDtos);
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
        List<RequestLectureDto> reqDtos = lectureMappings.stream()
                .map(mapping -> new RequestLectureDto(mapping.getLecture().getId(), mapping.getColor()))
                .collect(Collectors.toList());
        CreateTimeTableRequestDto dto = new CreateTimeTableRequestDto(table.getName(), reqDtos);

        given(userRepository.findById(table.getUser().getId())).willReturn(Optional.of(table.getUser()));
        given(lectureRepository.findAllById(any())).willReturn(lectures);
        given(timeTableRepository.save(tableCheckArgThat())).willReturn(table);

        // when
        Long id = service.create(table.getUser().getId(), dto);

        // then
        assertThat(id).isEqualTo(table.getId());
    }

    @Test
    @DisplayName("시간표 수정")
    void update() {
        // given
        List<RequestLectureDto> reqDtos = lectureMappings.stream()
                .map(mapping -> new RequestLectureDto(mapping.getLecture().getId(), mapping.getColor()))
                .collect(Collectors.toList());
        given(timeTableRepository.findById(table.getId())).willReturn(Optional.of(table));
        given(lectureRepository.findAllById(any())).willReturn(lectures);

        // when
        Long id = service.update(table.getUser().getId(), table.getId(), reqDtos);

        // then
        List<RequestLectureDto> tableLectures = table.getLectures().stream()
                .map(lecture -> new RequestLectureDto(lecture.getLecture().getId(), lecture.getColor()))
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
            assertThat(table.getLectures()).containsExactlyInAnyOrderElementsOf(lectureMappings);
            return true;
        });
    }
}