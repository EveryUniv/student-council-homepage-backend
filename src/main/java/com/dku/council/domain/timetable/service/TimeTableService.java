package com.dku.council.domain.timetable.service;

import com.dku.council.domain.timetable.exception.LectureNotFoundException;
import com.dku.council.domain.timetable.exception.TimeTableNotFoundException;
import com.dku.council.domain.timetable.model.dto.request.CreateTimeTableRequestDto;
import com.dku.council.domain.timetable.model.dto.request.RequestLectureDto;
import com.dku.council.domain.timetable.model.dto.response.TimeTableDto;
import com.dku.council.domain.timetable.model.dto.response.TimeTableInfoDto;
import com.dku.council.domain.timetable.model.entity.Lecture;
import com.dku.council.domain.timetable.model.entity.TimeTable;
import com.dku.council.domain.timetable.model.entity.TimeTableLecture;
import com.dku.council.domain.timetable.repository.LectureRepository;
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
    private final LectureRepository lectureRepository;
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

    public Long update(Long userId, Long tableId, List<RequestLectureDto> lectureDtos) {
        TimeTable table = timeTableRepository.findById(tableId)
                .orElseThrow(TimeTableNotFoundException::new);

        if (!table.getUser().getId().equals(userId))
            throw new TimeTableNotFoundException();

        table.getLectures().clear();
        appendLecturesEntity(table, lectureDtos);

        return table.getId();
    }

    public Long updateName(Long userId, Long tableId, String name) {
        TimeTable table = timeTableRepository.findById(tableId)
                .orElseThrow(TimeTableNotFoundException::new);

        if (!table.getUser().getId().equals(userId))
            throw new TimeTableNotFoundException();

        table.changeName(name);
        return table.getId();
    }

    private void appendLecturesEntity(TimeTable table, List<RequestLectureDto> lectureDtos) {
        List<Long> idList = lectureDtos.stream()
                .map(RequestLectureDto::getId)
                .collect(Collectors.toList());
        List<Lecture> lectures = lectureRepository.findAllById(idList);

        if (lectures.size() != lectureDtos.size()) {
            throw new LectureNotFoundException();
        }

        for (int i = 0; i < lectureDtos.size(); i++) {
            TimeTableLecture mapping = new TimeTableLecture(lectures.get(i), lectureDtos.get(i).getColor());
            mapping.changeTimeTable(table);
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
