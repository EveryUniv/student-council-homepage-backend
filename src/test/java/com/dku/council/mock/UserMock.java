package com.dku.council.mock;

import com.dku.council.domain.user.model.UserStatus;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.auth.role.UserRole;
import com.dku.council.util.EntityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

public class UserMock {

    public static final String STUDENT_ID = "12345678";
    public static final String PASSWORD = "abcdabab";
    public static final String NAME = "username";
    public static final String NICKNAME = "nickname";
    public static final Major MAJOR = MajorMock.create();

    public static List<User> createList(Major major, int size) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            users.add(create(major));
        }
        return users;
    }

    public static User createDummyMajor() {
        return createDummyMajor(RandomGen.nextLong());
    }

    public static User createMajor(String major, String department) {
        return create(RandomGen.nextLong(), NAME, UserRole.USER, MajorMock.create(major, department), null);
    }


    public static User createDummyMajor(Long userId) {
        return create(userId, NAME, UserRole.USER, MAJOR, null);
    }

    public static User create(Major major) {
        return create(RandomGen.nextLong(), major);
    }

    public static User create(Long userId, Major major) {
        return create(userId, NAME, UserRole.USER, major, null);
    }

    public static User create(String username, Major major) {
        return create(RandomGen.nextLong(), username, UserRole.USER, major, null);
    }

    public static User create(Long userId, String username, UserRole role, Major major, PasswordEncoder passwordEncoder) {
        String password = PASSWORD;

        if (passwordEncoder != null) {
            password = passwordEncoder.encode(password);
        }

        User user = User.builder()
                .studentId(STUDENT_ID)
                .password(password)
                .name(username)
                .status(UserStatus.ACTIVE)
                .role(role)
                .nickname(NICKNAME)
                .yearOfAdmission(2017)
                .academicStatus("재학")
                .major(major)
                .phone("01011112222")
                .build();

        EntityUtil.injectId(User.class, user, userId);
        return user;
    }
}
