package com.dku.council.domain.report.model.dto.list;

import com.dku.council.domain.report.model.entity.ReportCategory;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@Getter
public class ResponseReportCategoryDto {

    private final String id;

    private final String name;

    public ResponseReportCategoryDto(ReportCategory reportCategory, MessageSource messageSource) {
        this.id = reportCategory.name();
        this.name = messageSource.getMessage("report.category." + reportCategory.name().toLowerCase(), null, LocaleContextHolder.getLocale());
    }
}
