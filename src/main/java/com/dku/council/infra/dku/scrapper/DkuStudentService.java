package com.dku.council.infra.dku.scrapper;

import com.dku.council.global.config.webclient.ChromeAgentWebClient;
import com.dku.council.global.error.exception.UnexpectedResponseException;
import com.dku.council.infra.dku.exception.DkuFailedCrawlingException;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.StudentDuesStatus;
import com.dku.council.infra.dku.model.StudentInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.YearMonth;
import java.util.Optional;

@Slf4j
@Service
public class DkuStudentService extends DkuScrapper {

    private final String studentInfoApiPath;
    private final String feeInfoApiPath;

    public DkuStudentService(@ChromeAgentWebClient WebClient webClient,
                             @Value("${dku.student-info.info-api-path}") String studentInfoApiPath,
                             @Value("${dku.student-info.fee-api-path}") String feeInfoApiPath) {
        super(webClient);
        this.studentInfoApiPath = studentInfoApiPath;
        this.feeInfoApiPath = feeInfoApiPath;
    }


    /**
     * 학생 정보를 크롤링해옵니다.
     *
     * @param auth 인증 토큰
     * @return 학생 정보
     */
    public StudentInfo crawlStudentInfo(DkuAuth auth) {
        String html = requestWebInfo(auth, studentInfoApiPath);
        return parseStudentInfoHtml(html);
    }

    /**
     * 학생회비 납부 정보를 크롤링해옵니다.
     *
     * @param auth 인증 토큰
     * @return 학생 정보
     */
    public StudentDuesStatus crawlStudentDues(DkuAuth auth, YearMonth yearMonth) {
        String html = requestWebInfo(auth, feeInfoApiPath);
        return parseDuesStatusHtml(html, yearMonth);
    }

    private StudentDuesStatus parseDuesStatusHtml(String html, YearMonth yearMonth) {
        Document doc = Jsoup.parse(html);

        try {
            Element table = doc.getElementById("tbl_semList");
            Elements rows = table.select("tbody tr");
            if (rows.isEmpty()) {
                throw new DkuFailedCrawlingException(new UnexpectedResponseException("table is empty"));
            }

            for (int i = 0; i < rows.size(); i++) {
                Element row = rows.get(0);
                Elements cols = row.select("td");
                if (cols.size() != 6) {
                    throw new DkuFailedCrawlingException(new UnexpectedResponseException("table column size is not 6"));
                }

                if (!isThisPaidDues(yearMonth, cols)) continue;

                String needFees = cols.get(4).text();
                String paidFees = cols.get(5).text();

                if (needFees.equals(paidFees)) {
                    return StudentDuesStatus.PAID;
                } else {
                    return StudentDuesStatus.NOT_PAID;
                }
            }

            return StudentDuesStatus.NOT_PAID;
        } catch (NullPointerException e) {
            throw new DkuFailedCrawlingException(e);
        }
    }

    private static boolean isThisPaidDues(YearMonth yearMonth, Elements cols) {
        String year = cols.get(1).text();
        String semester = cols.get(2).text();
        String type = cols.get(3).text();

        if (!type.equals("학생회비")) {
            return false;
        }

        if (!String.valueOf(yearMonth.getYear()).equals(year)) {
            return false;
        }

        return semester.equals(getSemester(yearMonth));
    }

    private StudentInfo parseStudentInfoHtml(String html) {
        Document doc = Jsoup.parse(html);

        String studentName = getElementValueOrThrow(doc, "nm");
        String studentId = getElementValueOrThrow(doc, "stuid");
        String studentState = getElementValueOrThrow(doc, "scregStaNm");

        String major, department = "";
        int yearOfAdmission;

        try {
            // 사회과학대학 정치외교학과
            major = getElementValueOrThrow(doc, "pstnOrgzNm");
            major = major.trim();

            int spaceIdx = major.lastIndexOf(' ');
            if (spaceIdx >= 0) {
                department = major.substring(0, spaceIdx);
                major = major.substring(spaceIdx + 1);
            }

            String etrsYy = getElementValueOrThrow(doc, "etrsYy");
            yearOfAdmission = Integer.parseInt(etrsYy);
        } catch (Throwable t) {
            throw new DkuFailedCrawlingException(t);
        }

        return new StudentInfo(studentName, studentId, yearOfAdmission, studentState, major, department);
    }

    private String getElementValueOrThrow(Document doc, String id) {
        String value = Optional.ofNullable(doc.getElementById(id))
                .map(Element::val)
                .orElseThrow(() -> new DkuFailedCrawlingException(new NullPointerException(id)));
        if (value.isBlank()) {
            throw new DkuFailedCrawlingException(new NullPointerException(id));
        }
        return value;
    }
}
