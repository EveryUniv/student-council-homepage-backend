package com.dku.council.domain.timetable.service;

import com.dku.council.domain.timetable.model.entity.Lecture;
import com.dku.council.domain.timetable.model.entity.LectureTime;
import com.dku.council.domain.timetable.repository.LectureRepository;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.Subject;
import com.dku.council.infra.dku.scrapper.DkuAuthenticationService;
import com.dku.council.infra.dku.scrapper.DkuLectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureRetrieveService {

    private final DkuAuthenticationService dkuAuthenticationService;
    private final DkuLectureService dkuLectureService;
    private final LectureRepository lectureRepository;

    @Value("${dku.static-crawler.id}")
    private final String id;

    @Value("${dku.static-crawler.password}")
    private final String password;


    public void reloadLectures(YearMonth now) {
        DkuAuth auth = dkuAuthenticationService.loginWebInfo(id, password);
        List<Lecture> lectures = dkuLectureService.crawlLecture(auth, now).stream()
                .map(LectureRetrieveService::mapToLecture)
                .collect(Collectors.toList());

        lectureRepository.deleteAll();
        lectureRepository.saveAll(lectures);
    }

    private static Lecture mapToLecture(Subject subject) {
        Lecture lecture = Lecture.builder()
                .name(subject.getName())
                .professor(subject.getProfessor())
                .build();

        for (Subject.TimeAndPlace time : subject.getTimes()) {
            LectureTime lectureTime = LectureTime.builder()
                    .week(time.getDayOfWeek())
                    .place(time.getPlace())
                    .startTime(time.getFrom())
                    .endTime(time.getTo())
                    .build();
            lectureTime.changeLecture(lecture);
        }

        return lecture;
    }
}
