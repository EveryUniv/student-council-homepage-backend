package com.dku.council.domain.report.model.entity;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public enum ReportCategory {

    PROFANITY,

    FISHING,

    ADVERTISEMENT,

    POLITICS,

    PORNOGRAPHY,

    INAPPROPRIATE_CONTENT,

    FRAUD;

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
