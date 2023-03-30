package com.dku.council.mock;

import com.dku.council.domain.timetable.model.entity.TimeTable;

public class TimeTableMock {
    public static final String TABLE_NAME = "name";

    public static TimeTable createDummy() {
        return new TimeTable(UserMock.createDummyMajor(), TABLE_NAME);
    }
}
