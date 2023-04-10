package com.dku.council.domain.report.model.dto.list;

import com.dku.council.domain.report.model.entity.ReportCategory;
import lombok.Getter;

@Getter
public class ResponseReportCategoryListDto {

    private final int id;

    private final String name;

    public ResponseReportCategoryListDto(ReportCategory reportCategory) {
        this.id = reportCategory.getId();
        this.name = reportCategory.name();
    }
}
