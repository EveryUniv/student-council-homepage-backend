package com.dku.council.infra.bus.predict.impl;

import com.dku.council.domain.bus.holiday.service.HolidayService;
import com.dku.council.domain.bus.model.BusStation;
import com.dku.council.infra.bus.exception.CannotGetTimeTable;
import com.dku.council.infra.bus.predict.BusArrivalPredictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusTimeTablePredictService implements BusArrivalPredictService {

    public static final long FIRST_TIME_HOUR_OFFSET = 1;
    private final Map<String, TimeTable> busTimeTables = new HashMap<>();
    private final TimeTableParser timeTableParser;
    private final HolidayService holidayService;

    /**
     * 시간표를 기준으로 남은 시간을 예측합니다.
     * 예측할 수 없거나 버스가 없는 경우 null이 반환됩니다.
     * 버스가 있는 경우는 첫차-{@value FIRST_TIME_HOUR_OFFSET}시간 ~ 막차까지로 간주합니다.
     *
     * @param busNo 버스 번호
     * @param now   현재 시각
     * @return 예측된 남은 시간.
     */
    @Nullable
    public Duration remainingNextBusArrival(String busNo, BusStation station, LocalDateTime now) {
        String weekType = getWeekDayName(now);
        String stationDirName = station.name().replaceAll("_", "").toLowerCase();
        String tableFileName = String.format("/bustable/%s/%s/%s.table", weekType, stationDirName, busNo);

        TimeTable table = busTimeTables.get(tableFileName);
        LocalTime nowTime = now.toLocalTime();

        if (table == null) {
            try {
                table = timeTableParser.parse(tableFileName);
            } catch (CannotGetTimeTable e) {
                if (!(e.getCause() instanceof NullPointerException)) {
                    log.warn("Cannot parse time table", e);
                }
                return null;
            }
            busTimeTables.put(busNo, table);
        }

        if (isOutbound(table, nowTime)) {
            return null;
        }

        return table.remainingNextBusArrival(nowTime);
    }

    private String getWeekDayName(LocalDateTime dateTime) {
        DayOfWeek week = dateTime.getDayOfWeek();
        if (holidayService.isHoliday(dateTime.toLocalDate())) {
            return "holiday";
        }
        if (week == DayOfWeek.SATURDAY) {
            return "saturday";
        }
        return "weekday";
    }

    private static boolean isOutbound(TimeTable table, LocalTime now) {
        LocalTime first = table.getFirstTime();
        LocalTime last = table.getLastTime();

        if (Duration.between(first, last).abs().toHours() > 0) {
            first = first.minusHours(FIRST_TIME_HOUR_OFFSET);
        }

        if (first.isBefore(last)) {
            return now.isBefore(first) || now.isAfter(last);
        } else {
            return now.isAfter(last) && now.isBefore(first);
        }
    }
}
