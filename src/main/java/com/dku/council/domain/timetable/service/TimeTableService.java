package com.dku.council.domain.timetable.service;

import com.dku.council.domain.timetable.exception.TimeTableNotFoundException;
import com.dku.council.domain.timetable.model.dto.request.CreateTimeTableRequestDto;
import com.dku.council.domain.timetable.model.dto.response.LectureDto;
import com.dku.council.domain.timetable.model.dto.response.LectureTimeDto;
import com.dku.council.domain.timetable.model.dto.response.TimeTableDto;
import com.dku.council.domain.timetable.model.dto.response.TimeTableInfoDto;
import com.dku.council.domain.timetable.model.entity.Lecture;
import com.dku.council.domain.timetable.model.entity.LectureTime;
import com.dku.council.domain.timetable.model.entity.TimeTable;
import com.dku.council.domain.timetable.repository.TimeTableRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeTableService {

    private final UserRepository userRepository;
    private final TimeTableRepository timeTableRepository;

    @Transactional(readOnly = true)
    public List<TimeTableInfoDto> list(Long userId) {
        List<TimeTable> timeTables = timeTableRepository.findAllByUserId(userId);

        return timeTables.stream()
                .map(TimeTableInfoDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TimeTableDto findOne(Long userId, Long tableId) {
        TimeTable table = timeTableRepository.findById(tableId)
                .orElseThrow(TimeTableNotFoundException::new);

        if (!table.getUser().getId().equals(userId))
            throw new TimeTableNotFoundException();

        return new TimeTableDto(table);
    }

    public Long create(Long userId, CreateTimeTableRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        TimeTable timeTable = new TimeTable(user, dto.getName());
        appendLecturesEntity(timeTable, dto.getLectures());
        timeTable = timeTableRepository.save(timeTable);

        return timeTable.getId();
    }

    public Long update(Long userId, Long tableId, List<LectureDto> lectures) {
        TimeTable table = timeTableRepository.findById(tableId)
                .orElseThrow(TimeTableNotFoundException::new);

        if (!table.getUser().getId().equals(userId))
            throw new TimeTableNotFoundException();

        table.getLectures().clear();
        appendLecturesEntity(table, lectures);

        return table.getId();
    }

    public Long updateName(Long userId, Long tableId, String name) {
        TimeTable table = timeTableRepository.findById(tableId)
                .orElseThrow(TimeTableNotFoundException::new);

        if (!table.getUser().getId().equals(userId))
            throw new TimeTableNotFoundException();

        table.changeName(name);
        System.out.println(table.getName());
        return table.getId();
    }

    private void appendLecturesEntity(TimeTable table, List<LectureDto> lectures) {
        for (LectureDto lecDto : lectures) {
            Lecture lecture = Lecture.builder()
                    .name(lecDto.getName())
                    .professor(lecDto.getProfessor())
                    .place(lecDto.getPlace())
                    .build();

            for (LectureTimeDto timeDto : lecDto.getTimes()) {
                LectureTime lectureTime = LectureTime.builder()
                        .week(timeDto.getWeek())
                        .startTime(timeDto.getStart())
                        .endTime(timeDto.getEnd())
                        .build();
                lectureTime.changeLecture(lecture);
            }

            lecture.changeTimeTable(table);
        }
    }

    public Long delete(Long userId, Long tableId) {
        TimeTable table = timeTableRepository.findById(tableId)
                .orElseThrow(TimeTableNotFoundException::new);

        if (!table.getUser().getId().equals(userId))
            throw new TimeTableNotFoundException();

        timeTableRepository.delete(table);
        return table.getId();
    }
}
