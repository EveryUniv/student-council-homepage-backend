package com.dku.council.domain.report.model.entity;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public enum ReportCategory {

    PROFANITY,

    FISHING,

    ADVERTISEMENT("광고성 게시글"),

    POLITICS("정당, 정치인 비하 및 선거운동"),

    PORNOGRAPHY("음란물/불건전한 만남 및 대화"),

    INAPPROPRIATE_CONTENT("게시판 성격에 부적합"),

    FRAUD("유출/사칭/사기");



    public String getName(MessageSource messageSource) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("report.category." + this.name().toLowerCase(), null, locale);
    }

    public static ReportCategory fromValue(String value) {
        for (ReportCategory category : ReportCategory.values()) {
            if (category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + value + "]");
    }
}
