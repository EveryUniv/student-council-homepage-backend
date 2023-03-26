package com.dku.council.mock;

import com.dku.council.domain.tag.model.entity.Tag;
import com.dku.council.util.EntityUtil;

import java.util.ArrayList;
import java.util.List;

public class TagMock {

    public static Tag create() {
        return new Tag(RandomGen.nextUUID());
    }

    public static Tag create(Long id) {
        Tag tag = new Tag(RandomGen.nextUUID());
        EntityUtil.injectId(Tag.class, tag, id);
        return tag;
    }

    public static Tag create(Long id, String name){
        Tag tag = new Tag(name);
        EntityUtil.injectId(Tag.class, tag, id);
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
