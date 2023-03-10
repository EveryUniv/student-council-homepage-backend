package com.dku.council.mock;

import com.dku.council.domain.user.model.entity.Major;

public class MajorMock {
    public static Major create() {
        return new Major("MyMajor", "MyDepartment");
    }
}
