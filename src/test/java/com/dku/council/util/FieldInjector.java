package com.dku.council.util;

import java.lang.reflect.Field;

public class FieldInjector {
    public static <T> void inject(Class<T> clazz, T obj, String fieldName, Object value) {
        try {
            Field id = clazz.getDeclaredField(fieldName);
            id.setAccessible(true);
            id.set(obj, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
