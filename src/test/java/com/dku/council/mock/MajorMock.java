package com.dku.council.mock;

import com.dku.council.domain.user.model.entity.Major;

import java.util.ArrayList;
import java.util.List;

public class MajorMock {
    public static Major create() {
        return new Major("MyMajor", "MyDepartment");
    }

    public static List<Major> createList(int size) {
        List<Major> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(new Major("MyMajor" + i, "MyDepartment" + i));
        }
        return result;
    }

    public static Major create(String major, String department) {
        return new Major(major, department);
    }
}
