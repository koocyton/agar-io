package com.doopp.agar.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CommonUtil {

    // 对象转 map
    public static Map<String, Object> mutilObjectToMap(Object... objs) {
        Map<String, Object> map = new HashMap<>();
        for (Object obj : objs) {
            if (obj == null) {
                continue;
            }
            Class clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();
            try {
                for (Field field : fields) {
                    field.setAccessible(true);
                    map.put(field.getName(), field.get(obj));
                }
            } catch (Exception ignore) {
            }
        }
        return map;
    }
}
