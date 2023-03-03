package com.dku.council.domain.tag.model.dto;

import com.dku.council.domain.tag.model.entity.Tag;
import lombok.Getter;

@Getter
public class TagDto {
    private final Long id;
    private final String name;

    public TagDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public TagDto(Tag entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
