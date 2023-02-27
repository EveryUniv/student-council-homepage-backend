package com.dku.council.mock;

import com.dku.council.domain.user.model.MajorData;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.util.FieldInjector;

public class UserMock {

    public static User create() {
        return create(1L);
    }

    public static User create(Long userId) {
        User user = User.builder()
                .studentId("11111111")
                .password("pwd")
                .name("name")
                .major(new Major(MajorData.ADMIN))
                .phone("010-1111-2222")
                .build();

        FieldInjector.injectId(User.class, user, userId);
        return user;
    }
}
