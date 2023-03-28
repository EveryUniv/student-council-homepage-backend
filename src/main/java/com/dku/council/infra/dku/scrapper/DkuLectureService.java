package com.dku.council.infra.dku.scrapper;

import com.dku.council.global.config.webclient.ChromeAgentWebClient;
import com.dku.council.global.error.exception.UnexpectedResponseException;
import com.dku.council.infra.dku.exception.DkuFailedCrawlingException;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.MajorSubject;
import com.dku.council.infra.dku.model.Subject;
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
public class DkuLectureService extends DkuScrapper {

    private static final String DAY_OF_WEEK = "월화수목금토일";

    private final String lectureApiPath;

    public DkuLectureService(@ChromeAgentWebClient WebClient webClient,
                             @Value("${dku.lecture.api-path}") String lectureApiPath) {
        super(webClient);
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

            for (int i = 0; i < rows.size(); i++) {
                Element row = rows.get(i);
                Elements cols = row.select("td");
                if (cols.size() != columnCount) {
                    throw new DkuFailedCrawlingException(
                            new UnexpectedResponseException("table column size is not " + columnCount)
                    );
                }

                result.add(parser.parse(cols));
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
        return Subject.builder()
                .category(cols.get(1 + offset).text())
                .id(cols.get(2 + offset).text())
                .classNumber(Integer.parseInt(cols.get(3 + offset).text()))
                .name(cols.get(4 + offset).select("a").text())
                .credit(Integer.parseInt(cols.get(5 + offset).text()))
                .professor(cols.get(6 + offset).text())
                .times(parseTime(cols.get(7 + offset).text()))
                .build();
    }

    private static Subject parseMajorSubject(Elements cols) {
        Subject subject = parseSubject(cols, 2);
        return new MajorSubject(cols.get(1).text(),
                Integer.parseInt(cols.get(2).text()),
                subject);
    }

    private static List<Subject.TimeAndPlace> parseTime(String time) {
        List<Subject.TimeAndPlace> result = new ArrayList<>();

        String commonPlace = null;
        String[] tokens = time.split("/");

        int placeCount = countOfBracketOpen(time);

        if (placeCount == 0) { // 장소가 지정되지 않은 경우엔 공통 장소를 null로 설정
            commonPlace = "";
        } else if (placeCount == 1) { // 장소 지정이 끝에 하나만 있으면 공통으로 강의실을 설정
            int bracketOpenIndex = time.lastIndexOf("(");
            commonPlace = time.substring(bracketOpenIndex + 1, time.length() - 1);
            tokens = time.substring(0, bracketOpenIndex).split("/");
        }

        for (String token : tokens) {
            String place;
            if (commonPlace == null) {
                int bracketOpenIndex = token.lastIndexOf("(");
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

    private static int countOfBracketOpen(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '(') {
                count++;
            }
        }
        return count;
    }

    private static LocalTime classToLocalTime(int classTime) {
        return LocalTime.of(9, 0).plusMinutes(classTime * 30L);
    }

    private interface TableRowParser {
        Subject parse(Elements cols);
    }
}