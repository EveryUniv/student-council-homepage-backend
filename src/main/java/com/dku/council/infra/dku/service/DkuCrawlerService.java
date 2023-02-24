package com.dku.council.infra.dku.service;

import com.dku.council.domain.user.model.Major;
import com.dku.council.global.config.qualifier.ChromeAgentWebClient;
import com.dku.council.infra.dku.exception.DkuFailedCrawlingException;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.StudentInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DkuCrawlerService {

    @ChromeAgentWebClient
    private final WebClient webClient;

    private final MessageSource messageSource;

    @Value("${dku.student-info.api-path}")
    private final String studentInfoApiPath;


    /**
     * 학생 정보를 크롤링해옵니다.
     *
     * @param auth 인증 토큰
     * @return 학생 정보
     */
    public StudentInfo crawlStudentInfo(DkuAuth auth) {
        String html = requestStudentInfo(auth);
        return parseHtml(html);
    }

    private String requestStudentInfo(DkuAuth auth) {
        String result;
        try {
            result = webClient.post()
                    .uri(studentInfoApiPath)
                    .cookies(auth.authCookies())
                    .header("Referer", "https://webinfo.dankook.ac.kr/")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Throwable t) {
            throw new DkuFailedCrawlingException(t);
        }
        return result;
    }

    private StudentInfo parseHtml(String html) {
        Document doc = Jsoup.parse(html);

        String studentName = getElementValueOrNull(doc, "nm");
        String studentId = getElementValueOrNull(doc, "stuid");

        String pstnOrgzNm;
        Major major;
        int yearOfAdmission;

        try {
            pstnOrgzNm = getElementValueOrNull(doc, "pstnOrgzNm");
            String[] orgToken = pstnOrgzNm.trim().split(" ");
            pstnOrgzNm = orgToken[orgToken.length - 1];
            major = Major.of(messageSource, pstnOrgzNm);

            String etrsYy = getElementValueOrNull(doc, "etrsYy");
            yearOfAdmission = Integer.parseInt(etrsYy);
        } catch (Throwable t) {
            throw new DkuFailedCrawlingException(t);
        }

        if (major == null) {
            log.error("Unexpected major name: {}. It will be treated as {}", pstnOrgzNm, Major.NO_DATA.name());
            return new StudentInfo(studentName, studentId, yearOfAdmission, pstnOrgzNm);
        } else {
            return new StudentInfo(studentName, studentId, yearOfAdmission, major);
        }
    }

    private String getElementValueOrNull(Document doc, String id) {
        return Optional.ofNullable(doc.getElementById(id))
                .map(Element::val)
                .orElseThrow(() -> new DkuFailedCrawlingException(new NullPointerException(id)));
    }
}
