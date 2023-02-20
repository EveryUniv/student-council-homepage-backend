package com.dku.council.infra.dku.service;

import com.dku.council.domain.user.Major;
import com.dku.council.global.config.qualifier.ChromeAgentWebClient;
import com.dku.council.infra.dku.exception.DkuFailedCrawlingException;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.StudentInfo;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

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

        String studentId = getElementValueOrNull(doc, "stuid");
        Major major;
        int yearOfAdmission;

        try {
            String pstnOrgzNm = getElementValueOrNull(doc, "pstnOrgzNm");
            pstnOrgzNm = pstnOrgzNm.trim().split(" ")[1];
            major = Major.of(messageSource, pstnOrgzNm);

            String etrsYy = getElementValueOrNull(doc, "etrsYy");
            yearOfAdmission = Integer.parseInt(etrsYy);
        } catch (Throwable t) {
            throw new DkuFailedCrawlingException(t);
        }

        return new StudentInfo(studentId, yearOfAdmission, major);
    }

    private String getElementValueOrNull(Document doc, String id) {
        return Optional.ofNullable(doc.getElementById(id))
                .map(Element::val)
                .orElseThrow(() -> new DkuFailedCrawlingException(new NullPointerException(id)));
    }
}
