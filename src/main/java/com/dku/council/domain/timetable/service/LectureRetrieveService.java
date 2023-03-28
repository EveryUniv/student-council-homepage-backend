package com.dku.council.domain.timetable.service;

import com.dku.council.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureRetrieveService {

    private final UserRepository userRepository;
    private final TimeTableService timeTableService;

    public void reloadLectures(YearMonth now) {

    }
}
