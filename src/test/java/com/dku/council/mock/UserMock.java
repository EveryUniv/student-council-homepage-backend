package com.dku.council.mock;

import com.dku.council.domain.user.model.MajorData;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.auth.role.UserRole;
import com.dku.council.util.FieldInjector;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserMock {

    public static final String STUDENT_ID = "12345678";
    public static final String PASSWORD = "abcdabab";

    public static User create() {
        return create(1L);
    }

    public static User create(Long userId) {
        return create(userId, null);
    }

    public static User create(Long userId, PasswordEncoder passwordEncoder) {
        return create(userId, UserRole.USER, passwordEncoder);
    }

    public static User create(Long userId, UserRole role, PasswordEncoder passwordEncoder) {
        String password = PASSWORD;

        if (passwordEncoder != null) {
            password = passwordEncoder.encode(password);
        }

        User user = User.builder()
                .studentId(STUDENT_ID)
                .password(password)
                .name("name")
                .role(role)
                .major(new Major(MajorData.ADMIN))
                .phone("010-1111-2222")
                .build();

        FieldInjector.injectId(User.class, user, userId);
        return user;
    }
}
