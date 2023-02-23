package com.dku.council.domain.user.model;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public enum Major {
    KOREAN_LITERATURE(Department.LIBERAL),
    HISTORY(Department.LIBERAL),
    PHILOSOPHY(Department.LIBERAL),
    ENGLISH_HUMANITIES(Department.LIBERAL),
    LAW(Department.LAW),
    POLITICAL(Department.SOCIAL),
    ADMINISTRATION(Department.SOCIAL),
    SOCIETY(Department.SOCIAL),
    COMMUNICATION(Department.SOCIAL),
    COUNSELING(Department.SOCIAL),
    ECONOMICS(Department.BUSINESS_ECONOMICS),
    COMMERCE(Department.BUSINESS_ECONOMICS),
    BUSINESS(Department.BUSINESS_ECONOMICS),
    INDUSTRY(Department.BUSINESS_ECONOMICS),
    INTERNATIONAL(Department.BUSINESS_ECONOMICS),
    ELECTRON(Department.ENGINEERING),
    POLYMER(Department.ENGINEERING),
    CIVIL(Department.ENGINEERING),
    MECHANICAL(Department.ENGINEERING),
    CHEMISTRY(Department.ENGINEERING),
    ARCHITECTURE(Department.ENGINEERING),
    SOFTWARE(Department.SOFTWARE),
    COMPUTER_SCIENCE(Department.SOFTWARE),
    APPLIED_COMPUTER_SCIENCE(Department.ENGINEERING),
    MOBILE_SYSTEM(Department.SOFTWARE),
    STATISTICS(Department.SOFTWARE),
    SECURITY(Department.SOFTWARE),
    SOFTWARE_CONVERGENCE(Department.SOFTWARE),
    EDU_CHINESE(Department.EDUCATION),
    EDU_SPECIAL(Department.EDUCATION),
    EDU_MATHEMATICS(Department.EDUCATION),
    EDU_SCIENCE(Department.EDUCATION),
    EDU_PHYSICAL(Department.EDUCATION),
    EDU_TEACHING(Department.EDUCATION),
    POTTERY(Department.MUSIC_ART),
    DESIGN(Department.MUSIC_ART),
    FILM(Department.MUSIC_ART),
    DANCE(Department.MUSIC_ART),
    MUSIC(Department.MUSIC_ART),
    LIBERAL_ARTS(Department.LIBERAL_ARTS),
    GRADUATE(Department.GRADUATE),
    ADMIN(Department.ADMIN),
    NO_DATA(Department.NO_DATA);

    private final Department department;

    Major(Department department) {
        this.department = department;
    }

    public Department getDepartment() {
        return department;
    }

    public String getName(MessageSource source) {
        Locale locale = LocaleContextHolder.getLocale();
        return source.getMessage("Major." + name(), null, locale);
    }

    /**
     * 학과 이름으로 Major를 찾습니다. 학과명은 소속대학을 포함하지 않는 이름을 의미합니다.
     * 예를 들면 컴퓨터공학과, 국어국문학과 등...
     *
     * @param source MessageSource
     * @param major  학과 명
     * @return 매칭되는 Major.
     */
    public static Major of(MessageSource source, String major) {
        major = major.replace(" ", "")
                .replace("\t", "");

        Major[] values = values();
        for (Major majorEnum : values) {
            if (majorEnum.getName(source).equals(major)) {
                return majorEnum;
            }
        }

        return null;
    }

    public boolean isEmpty() {
        return this == NO_DATA;
    }
}
