package com.dku.council.domain.admin.dto;

import com.dku.council.domain.tag.model.entity.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TagPageDto {
    private final Long id;
    private final String name;

    public TagPageDto(Tag tag){
        this.id = tag.getId();
        this.name = tag.getName();
    }

}
