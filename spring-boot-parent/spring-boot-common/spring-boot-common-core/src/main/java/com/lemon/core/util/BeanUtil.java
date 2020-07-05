package com.lemon.core.util;

public class BeanUtil<Source, Target> {
    public static <Target>Target sourceToTarget(Object sourceEntity, Class<Target> targetClass) {
        // 判断dto是否为空!
        if (sourceEntity == null) {
            return null;
        }
        // 判断DoClass 是否为空
        if (targetClass == null) {
            return null;
        }
        try {
            Target instance = targetClass.newInstance();
            org.springframework.beans.BeanUtils.copyProperties(sourceEntity, instance);
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
