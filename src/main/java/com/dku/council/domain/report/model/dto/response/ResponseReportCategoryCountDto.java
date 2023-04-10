package com.dku.council.domain.report.model.dto.response;

import com.dku.council.domain.report.model.entity.Report;
import lombok.Getter;
import org.springframework.context.MessageSource;

@Getter
public class ResponseReportCategoryCountDto {

    private final String name;

    private final long reportedCount;

    public ResponseReportCategoryCountDto(String name, long reportedCount) {
        this.name = name;
        this.reportedCount = reportedCount;
    }
}
