package com.dku.council.mock;

import com.dku.council.domain.tag.model.entity.Tag;
import com.dku.council.util.FieldInjector;

import java.util.ArrayList;
import java.util.List;

public class CategoryMock {

    public static Tag create() {
        return new Tag(RandomGen.nextUUID());
    }

    public static Tag create(Long id) {
        Tag tag = new Tag(RandomGen.nextUUID());
        FieldInjector.injectId(Tag.class, tag, id);
        return tag;
    }

    public static List<Tag> createList(int size) {
        List<Tag> categories = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            categories.add(create());
        }
        return categories;
    }
}
