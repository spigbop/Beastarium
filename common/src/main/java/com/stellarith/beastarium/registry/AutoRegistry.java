package com.stellarith.beastarium.registry;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class AutoRegistry {
    public static <T> Map<T, String> getObjectsFrom(Class<?> registryClass, Class<T> type) {
        Map<T, String> results = new LinkedHashMap<>();

        for (Field field: registryClass.getDeclaredFields()) {
            try{
                Object value = field.get(null);

                if(type.isInstance(value)) {
                    results.put(type.cast(value), toSnakeCase(field.getName()));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access: " + field.getName(), e);
            }
        }

        return results;
    }

    private static String toSnakeCase(String input) {
        return input.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
