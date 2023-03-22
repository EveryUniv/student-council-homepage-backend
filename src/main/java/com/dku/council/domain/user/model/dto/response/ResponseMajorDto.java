package com.dku.council.domain.user.model.dto.response;

import com.dku.council.domain.user.model.entity.Major;
import lombok.Getter;

@Getter
public class ResponseMajorDto {
    private final Long id;
    private final String department;
    private final String name;

    public ResponseMajorDto(Major major) {
        this.id = major.getId();
        this.department = major.getDepartment();
        this.name = major.getName();
    }
}
