package com.dku.council.mock;

import com.dku.council.domain.timetable.model.entity.LectureTemplate;
import com.dku.council.util.EntityUtil;

import java.util.ArrayList;
import java.util.List;

public class LectureTemplateMock {

    public static List<LectureTemplate> createList(int size) {
        List<LectureTemplate> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            LectureTemplate e = create(i);
            EntityUtil.injectId(LectureTemplate.class, e, (long) i);
            result.add(e);
        }
        return result;
    }

    public static LectureTemplate create(int i) {
        return LectureTemplate.builder()
                .category("세계시민역량")
                .lectureId("539250")
                .classNumber(1)
                .name("lecture" + i)
                .credit(3)
                .professor("professor" + i)
                .timesJson("[{\"week\":\"TUESDAY\",\"start\":\"16:00:00\",\"end\":\"17:30:00\",\"place\":\"place1\"}" +
                        ",{\"week\":\"THURSDAY\",\"start\":\"12:00:00\",\"end\":\"15:30:00\",\"place\":\"place2\"}]")
                .build();
    }
}
