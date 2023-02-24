package com.dku.council.domain.user.model;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public enum Department {

    LIBERAL,
    LAW,
    SOCIAL,
    BUSINESS_ECONOMICS,
    ENGINEERING,
    SOFTWARE,
    EDUCATION,
    MUSIC_ART,
    LIBERAL_ARTS,
    GRADUATE,
    ADMIN,
    NO_DATA;

    public String getName(MessageSource source) {
        Locale locale = LocaleContextHolder.getLocale();
        return source.getMessage("Department." + name(), null, locale);
    }
}