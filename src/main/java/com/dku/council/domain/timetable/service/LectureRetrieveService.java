package com.dku.council.domain.timetable.service;

import com.dku.council.domain.timetable.model.dto.TimePromise;
import com.dku.council.domain.timetable.model.entity.LectureTemplate;
import com.dku.council.domain.timetable.repository.LectureTemplateRepository;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.MajorSubject;
import com.dku.council.infra.dku.model.Subject;
import com.dku.council.infra.dku.scrapper.DkuAuthenticationService;
import com.dku.council.infra.dku.scrapper.DkuLectureService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper mapper;
    private final DkuAuthenticationService dkuAuthenticationService;
    private final DkuLectureService dkuLectureService;
    private final LectureTemplateRepository lectureTemplateRepository;

    @Value("${dku.static-crawler.id}")
    private final String id;

    @Value("${dku.static-crawler.password}")
    private final String password;


    public void reloadLectures(YearMonth now) {
        DkuAuth auth = dkuAuthenticationService.loginWebInfo(id, password);
        List<LectureTemplate> lectures = dkuLectureService.crawlLecture(auth, now).stream()
                .map(this::mapToLecture)
                .collect(Collectors.toList());

        lectureTemplateRepository.deleteAll();
        lectureTemplateRepository.saveAll(lectures);
    }

    private LectureTemplate mapToLecture(Subject subject) {
        List<TimePromise> times = subject.getTimes().stream()
                .map(t -> new TimePromise(t.getFrom(), t.getTo(), t.getDayOfWeek(), t.getPlace()))
                .collect(Collectors.toList());

        LectureTemplate.LectureTemplateBuilder builder = LectureTemplate.builder()
                .lectureId(subject.getId())
                .category(subject.getCategory())
                .name(subject.getName())
                .professor(subject.getProfessor())
                .classNumber(subject.getClassNumber())
                .credit(subject.getCredit())
                .timesJson(TimePromise.serialize(mapper, times));

        if (subject instanceof MajorSubject) {
            MajorSubject majorSubject = (MajorSubject) subject;
            builder.major(majorSubject.getMajor())
                    .grade(majorSubject.getGrade());
        }

        return builder.build();
    }
}
