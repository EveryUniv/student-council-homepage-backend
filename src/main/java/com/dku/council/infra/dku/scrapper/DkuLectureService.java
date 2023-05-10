package com.dku.council.infra.dku.scrapper;

import com.dku.council.global.config.webclient.ChromeAgentWebClient;
import com.dku.council.global.error.exception.UnexpectedResponseException;
import com.dku.council.infra.dku.exception.DkuFailedCrawlingException;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.MajorSubject;
import com.dku.council.infra.dku.model.Subject;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DkuLectureService extends DkuScrapper {

    private static final String DAY_OF_WEEK = "월화수목금토일";

    private final String lectureApiPath;

    public DkuLectureService(@ChromeAgentWebClient WebClient webClient,
                             @Value("${dku.lecture.api-path}") String lectureApiPath) {
        super(webClient.mutate()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build());
        this.lectureApiPath = lectureApiPath;
    }

    /**
     * 모든 수업 정보를 크롤링합니다.
     *
     * @param auth 인증 토큰
     * @return 학사 일정
     */
    public List<Subject> crawlLecture(DkuAuth auth, YearMonth now) {
        String year = String.valueOf(now.getYear());
        String semester = getSemester(now);

        String html = request(auth, year, semester, "2"); // 교양
        List<Subject> result = new ArrayList<>(parseSubjects(html, "culLctTmtblDscTbl",
                8, DkuLectureService::parseSubject));

        html = request(auth, year, semester, "1"); // 전공
        result.addAll(parseSubjects(html, "mjLctTmtblDscTbl",
                10, DkuLectureService::parseMajorSubject));

        return result;
    }

    private String request(DkuAuth auth, String year, String semester, String type) {
        String result;
        try {
            result = makeRequestWebInfo(auth, lectureApiPath)
                    .body(BodyInserters.fromFormData("yy", year)
                            .with("semCd", semester)
                            .with("qrySxn", type)
                            .with("lesnPlcCd", "1")
                            .with("collCd", "2000000989"))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Throwable t) {
            throw new DkuFailedCrawlingException(t);
        }

        return result;
    }

    private List<Subject> parseSubjects(String html, String tableName, int columnCount, TableRowParser parser) {
        Document doc = Jsoup.parse(html);
        List<Subject> result = new ArrayList<>();

        try {
            Element table = doc.getElementById(tableName);
            Elements rows = table.select("tbody tr");
            if (rows.isEmpty()) {
                throw new DkuFailedCrawlingException(new UnexpectedResponseException("table is empty"));
            }

            for (Element row : rows) {
                Elements cols = row.select("td");
                if (cols.size() != columnCount) {
                    throw new DkuFailedCrawlingException(
                            new UnexpectedResponseException("table column size is not " + columnCount)
                    );
                }

                Subject subject = parser.parse(cols);
                if (subject != null) {
                    result.add(subject);
                }
            }

            return result;
        } catch (NullPointerException e) {
            throw new DkuFailedCrawlingException(e);
        }
    }

    private static Subject parseSubject(Elements cols) {
        return parseSubject(cols, 0);
    }

    private static Subject parseSubject(Elements cols, int offset) {
        String timeText = cols.get(7 + offset).text();

        if (timeText.isBlank()) return null;

        List<Subject.TimeAndPlace> times;
        try {
            times = parseTime(timeText);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse time: {}", timeText);
            throw new DkuFailedCrawlingException(e);
        }

        return Subject.builder()
                .category(cols.get(1 + offset).text())
                .id(cols.get(2 + offset).text())
                .classNumber(Integer.parseInt(cols.get(3 + offset).text()))
                .name(cols.get(4 + offset).select("a").text())
                .credit(Integer.parseInt(cols.get(5 + offset).text()))
                .professor(cols.get(6 + offset).text())
                .times(times)
                .build();
    }

    private static Subject parseMajorSubject(Elements cols) {
        Subject subject = parseSubject(cols, 2);
        if (subject == null) return null;

        return new MajorSubject(cols.get(1).text(),
                Integer.parseInt(cols.get(2).text()),
                subject);
    }

    private static List<Subject.TimeAndPlace> parseTime(String time) {
        List<Subject.TimeAndPlace> result = new ArrayList<>();

        String commonPlace = null;
        String[] tokens = time.split("/");

        int placeCount = countOfPlace(tokens);

        if (placeCount == 0) { // 장소가 지정되지 않은 경우엔 공통 장소를 null로 설정
            commonPlace = "";
        } else if (placeCount == 1) { // 장소 지정이 끝에 하나만 있으면 공통으로 강의실을 설정
            int bracketOpenIndex = time.indexOf("(");
            commonPlace = time.substring(bracketOpenIndex + 1, time.length() - 1);
            tokens = time.substring(0, bracketOpenIndex).split("/");
        }

        for (String token : tokens) {
            String place;
            if (commonPlace == null) {
                int bracketOpenIndex = token.indexOf("(");
                place = token.substring(bracketOpenIndex + 1, token.length() - 1);
                token = token.substring(0, bracketOpenIndex);
            } else {
                place = commonPlace;
            }

            if (place.isEmpty()) {
                place = null;
            }

            DayOfWeek dayOfWeek = DayOfWeek.of(DAY_OF_WEEK.indexOf(token.charAt(0)) + 1);
            String[] times = token.substring(1).split(",");

            int startClassTime = Integer.parseInt(times[0]) - 1;
            LocalTime start = classToLocalTime(startClassTime);

            int endClassTime = Integer.parseInt(times[times.length - 1]);
            LocalTime end = classToLocalTime(endClassTime);

            result.add(new Subject.TimeAndPlace(dayOfWeek, start, end, place));
        }

        return result;
    }

    private static int countOfPlace(String[] tokens) {
        int count = 0;
        for (String token : tokens) {
            if (token.indexOf('(') > 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * 교시를 보고 종료 시각을 구합니다.
     * 야간 강의는 18:00 부터 55분 단위로 바뀝니다.
     *
     * @example 교시별 종료 예시
     * 1교시 9:30 종료
     * 18교시 18:00 종료
     * 19교시 18:55 종료
     * 20교시 19:50 종료
     */
    private static LocalTime classToLocalTime(int classTime) {
        if (classTime >= 18) {
            return LocalTime.of(18, 0).plusMinutes((classTime - 18) * 55L);
        } else {
            return LocalTime.of(9, 0).plusMinutes(classTime * 30L);
        }
    }

    private interface TableRowParser {
        Subject parse(Elements cols);
    }
}