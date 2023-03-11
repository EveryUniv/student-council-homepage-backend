package com.dku.council.util;

import com.dku.council.global.base.BaseEntity;

import java.util.List;

public class EntityUtil {

    public static <T> void injectId(Class<T> clazz, T obj, Long id) {
        FieldReflector.inject(clazz, obj, "id", id);
    }

    public static <T extends BaseEntity> Integer[] getIdArray(List<T> entities) {
        int size = entities.size();
        Integer[] ids = new Integer[size];
        for (int i = 0; i < size; i++) {
            ids[i] = (int) entities.get(i).getId().longValue();
        }
        return ids;
    }
}
