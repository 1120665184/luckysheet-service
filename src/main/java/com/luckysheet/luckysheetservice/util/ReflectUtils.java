package com.luckysheet.luckysheetservice.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * @ClassName ReflectUtils
 * @Description 反射工具
 * @Author Quyq
 * @Date 2022/7/14 15:06
 **/
@Slf4j
public class ReflectUtils {

    /**
     * 通过属性名获取对应的值
     * @param target
     * @param fieldName
     * @return
     */
    public static Object getFieldValue(Object target , String fieldName){
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields){
            if(field.getName().equals(fieldName)){
                field.setAccessible(true);
                try {
                    return field.get(target);
                } catch (IllegalAccessException e) {
                    log.error("ReflectUtils getFieldValue:",e);
                }
            }

        }
        return null;
    }

    /**
     * 通过属性名设置值
     * @param target
     * @param fieldName
     * @param setValue
     * @return
     */
    public static boolean setFieldValue(Object target , String fieldName , Object setValue){
        try {
            Field declaredField = target.getClass().getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            declaredField.set(target , setValue);
            return true;
        } catch (NoSuchFieldException|IllegalAccessException e) {
            log.error("ReflectUtils setFieldValue:",e);
        }
        return false;
    }

}
