package com.dku.council.domain.timetable.service;

import com.dku.council.domain.timetable.exception.TimeTableNotFoundException;
import com.dku.council.domain.timetable.model.dto.request.CreateTimeTableRequestDto;
import com.dku.council.domain.timetable.model.dto.response.LectureDto;
import com.dku.council.domain.timetable.model.dto.response.LectureTimeDto;
import com.dku.council.domain.timetable.model.dto.response.TimeTableDto;
import com.dku.council.domain.timetable.model.entity.Lecture;
import com.dku.council.domain.timetable.model.entity.LectureTime;
import com.dku.council.domain.timetable.model.entity.TimeTable;
import com.dku.council.domain.timetable.repository.TimeTableRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeTableService {

    private final UserRepository userRepository;
    private final TimeTableRepository timeTableRepository;


    public List<TimeTableDto> list(Long userId) {
        List<TimeTable> timeTables = timeTableRepository.findAllByUserId(userId);

        return timeTables.stream()
                .map(TimeTableDto::new)
                .collect(Collectors.toList());
    }

    public List<LectureDto> findOne(Long userId, Long tableId) {
        TimeTable table = timeTableRepository.findById(tableId)
                .orElseThrow(TimeTableNotFoundException::new);

        if (!table.getUser().getId().equals(userId))
            throw new TimeTableNotFoundException();

        return table.getLectures().stream()
                .map(LectureDto::new)
                .collect(Collectors.toList());
    }

    public Long create(Long userId, CreateTimeTableRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        TimeTable timeTable = createTimeTableEntity(user, dto.getName(), dto.getLectures());
        timeTable = timeTableRepository.save(timeTable);

        return timeTable.getId();
    }

    public Long update(Long userId, Long tableId, List<LectureDto> lectures) {
        TimeTable table = timeTableRepository.findById(tableId)
                .orElseThrow(TimeTableNotFoundException::new);

        if (!table.getUser().getId().equals(userId))
            throw new TimeTableNotFoundException();

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        timeTableRepository.delete(table);
        table = timeTableRepository.save(createTimeTableEntity(user, table.getName(), lectures));

        return table.getId();
    }

    private TimeTable createTimeTableEntity(User user, String name, List<LectureDto> lectures) {
        TimeTable timeTable = new TimeTable(user, name);

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

            lecture.changeTimeTable(timeTable);
        }

        return timeTable;
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
