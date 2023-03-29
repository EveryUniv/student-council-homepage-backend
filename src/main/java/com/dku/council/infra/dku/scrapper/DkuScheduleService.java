package com.dku.council.infra.dku.scrapper;

import com.dku.council.global.config.webclient.ChromeAgentWebClient;
import com.dku.council.infra.dku.exception.DkuFailedCrawlingException;
import com.dku.council.infra.dku.model.DkuAuth;
import com.dku.council.infra.dku.model.ScheduleInfo;
import com.dku.council.infra.dku.model.ScheduleResponse;
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
public class DkuScheduleService extends DkuScrapper {

    private static final DateTimeFormatter SCHEDULE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final String scheduleApiPath;

    public DkuScheduleService(@ChromeAgentWebClient WebClient webClient,
                              @Value("${dku.schedule.api-path}") String scheduleApiPath) {
        super(webClient);
        this.scheduleApiPath = scheduleApiPath;
    }


    /**
     * 학사 일정을 크롤링해옵니다.
     *
     * @param auth 인증 토큰
     * @return 학사 일정
     */
    public List<ScheduleInfo> crawlSchedule(DkuAuth auth, LocalDate from, LocalDate to) {
        ScheduleResponse response = request(auth, from, to);
        try {
            return parse(response.getData());
        } catch (Throwable e) {
            throw new DkuFailedCrawlingException(e);
        }
    }

    private ScheduleResponse request(DkuAuth auth, LocalDate from, LocalDate to) {
        ScheduleResponse result = requestPortal(auth,
                String.format(scheduleApiPath,
                        SCHEDULE_DATE_FORMAT.format(from),
                        SCHEDULE_DATE_FORMAT.format(to)),
                ScheduleResponse.class
        );

        if (!result.isSuccess()) {
            throw new DkuFailedCrawlingException(result.getMsg());
        }

        return result;
    }

    private List<ScheduleInfo> parse(String data) throws ParseException {
        List<ScheduleInfo> result = new ArrayList<>();
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

    private static ScheduleInfo parseSchedule(JSONObject obj) {
        String title = obj.get("taskName").toString();
        LocalDate fromDate = LocalDate.parse(obj.get("startDt").toString(), SCHEDULE_DATE_FORMAT);
        LocalDate toDate = LocalDate.parse(obj.get("endDt").toString(), SCHEDULE_DATE_FORMAT);
        return new ScheduleInfo(title, fromDate, toDate);
    }
}
