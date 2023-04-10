package com.dku.council.domain.report.model.entity;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public enum ReportCategory {

    PROFANITY(0),

    FISHING(1),

    ADVERTISEMENT(2),

    POLITICS(3),

    PORNOGRAPHY(4),

    INAPPROPRIATE_CONTENT(5),

    FRAUD(6);

    private final int id;

    ReportCategory(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

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
