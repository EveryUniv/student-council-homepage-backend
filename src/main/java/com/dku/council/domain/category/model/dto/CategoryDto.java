package com.dku.council.domain.category.model.dto;

import com.dku.council.domain.category.model.entity.Category;
import lombok.Getter;

@Getter
public class CategoryDto {
    private final Long id;
    private final String name;

    public CategoryDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CategoryDto(Category entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
