package com.dku.council.mock;

import com.dku.council.domain.category.model.entity.Category;
import com.dku.council.util.FieldInjector;

import java.util.ArrayList;
import java.util.List;

public class CategoryMock {

    public static Category create() {
        return new Category(RandomGen.nextUUID());
    }

    public static Category create(Long id) {
        Category category = new Category(RandomGen.nextUUID());
        FieldInjector.injectId(Category.class, category, id);
        return category;
    }

    public static List<Category> createList(int size) {
        List<Category> categories = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            categories.add(create());
        }
        return categories;
    }
}
