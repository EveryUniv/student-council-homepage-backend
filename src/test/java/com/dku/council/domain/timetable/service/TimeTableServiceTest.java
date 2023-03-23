package com.dku.council.domain.timetable.service;

import com.dku.council.domain.timetable.exception.TimeTableNotFoundException;
import com.dku.council.domain.timetable.model.dto.request.CreateTimeTableRequestDto;
import com.dku.council.domain.timetable.model.dto.response.LectureDto;
import com.dku.council.domain.timetable.model.dto.response.TimeTableDto;
import com.dku.council.domain.timetable.model.dto.response.TimeTableInfoDto;
import com.dku.council.domain.timetable.model.entity.TimeTable;
import com.dku.council.domain.timetable.repository.TimeTableRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TimeTableServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TimeTableRepository timeTableRepository;

    @InjectMocks
    private TimeTableService service;

    private TimeTable table;

    @BeforeEach
    void setup() {
        table = TimeTableMock.createDummy();
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
        assertThat(actualNames).containsAnyElementsOf(tableNames);
    }

    @Test
    @DisplayName("시간표 단건 조회")
    void findOne() {
        // given
        given(timeTableRepository.findById(table.getId())).willReturn(Optional.of(table));

        // when
        TimeTableDto actual = service.findOne(table.getUser().getId(), table.getId());

        // then
        List<LectureDto> expected = getLectureDtos(table);
        assertThat(actual.getId()).isEqualTo(table.getId());
        assertThat(actual.getName()).isEqualTo(table.getName());
        assertThat(actual.getLectures()).containsAnyElementsOf(expected);
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
        List<LectureDto> expectedLectures = getLectureDtos(table);
        CreateTimeTableRequestDto dto = new CreateTimeTableRequestDto(table.getName(), expectedLectures);
        given(userRepository.findById(table.getUser().getId())).willReturn(Optional.of(table.getUser()));
        given(timeTableRepository.save(tableCheckArgThat())).willReturn(table);

        // when
        Long id = service.create(table.getUser().getId(), dto);

        // then
        assertThat(id).isEqualTo(table.getId());
    }

    private static List<LectureDto> getLectureDtos(TimeTable table) {
        return table.getLectures().stream()
                .map(LectureDto::new)
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("시간표 수정")
    void update() {
        // given
        List<LectureDto> expectedLectures = getLectureDtos(table);
        given(timeTableRepository.findById(table.getId())).willReturn(Optional.of(table));

        // when
        Long id = service.update(table.getUser().getId(), table.getId(), expectedLectures);

        // then
        assertThat(id).isEqualTo(table.getId());
        assertThat(getLectureDtos(table)).containsAnyElementsOf(expectedLectures);
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
            assertThat(getLectureDtos(t)).containsAnyElementsOf(getLectureDtos(table));
            return true;
        });
    }
}