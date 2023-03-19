package com.dku.council.infra.dku.service;

import com.dku.council.global.config.webclient.ChromeAgentWebClient;
import com.dku.council.infra.dku.exception.DkuFailedCrawlingException;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.Schedule;
import com.dku.council.infra.dku.model.ScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DkuScheduleService {

    private static final DateTimeFormatter SCHEDULE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @ChromeAgentWebClient
    private final WebClient webClient;

    @Value("${dku.schedule.api-path}")
    private final String scheduleApiPath;


    /**
     * 학사 일정을 크롤링해옵니다.
     *
     * @param auth 인증 토큰
     * @return 학사 일정
     */
    public List<Schedule> crawlSchedule(DkuAuth auth) {
        ScheduleResponse response = request(auth);
        try {
            return parse(response.getData());
        } catch (Throwable e) {
            throw new DkuFailedCrawlingException(e);
        }
    }

    private ScheduleResponse request(DkuAuth auth) {
        ScheduleResponse result;
        try {
            result = webClient.post()
                    .uri(scheduleApiPath)
                    .cookies(auth.authCookies())
                    .header("Referer", "https://portal.dankook.ac.kr/p/S01/")
                    .retrieve()
                    .bodyToMono(ScheduleResponse.class)
                    .block();
        } catch (Throwable t) {
            throw new DkuFailedCrawlingException(t);
        }

        if (result == null) {
            throw new DkuFailedCrawlingException("Failed to crawl schedule");
        }

        if (!result.isSuccess()) {
            throw new DkuFailedCrawlingException(result.getMsg());
        }

        return result;
    }

    private List<Schedule> parse(String data) throws ParseException {
        List<Schedule> result = new ArrayList<>();
        JSONParser parser = new JSONParser();

        // parse 'data'
        JSONObject obj = (JSONObject) parser.parse(data);
        data = (String) ((JSONArray) obj.get("schArr")).get(0);

        // parse 'schArr'
        JSONArray schArr = (JSONArray) parser.parse(data);
        for (Object dateTasksObj : schArr) {
            JSONObject dateTasks = (JSONObject) dateTasksObj;
            JSONArray cttTaskList = (JSONArray) dateTasks.get("cttTaskList");
            for (Object taskObj : cttTaskList) {
                JSONObject task = (JSONObject) taskObj;
                result.add(parseSchedule(task));
            }
        }

        return result;
    }

    private static Schedule parseSchedule(JSONObject obj) {
        String title = obj.get("taskName").toString();
        LocalDate fromDate = LocalDate.parse(obj.get("startDt").toString(), SCHEDULE_DATE_FORMAT);
        LocalDate toDate = LocalDate.parse(obj.get("endDt").toString(), SCHEDULE_DATE_FORMAT);
        return new Schedule(title, fromDate, toDate);
    }
}
