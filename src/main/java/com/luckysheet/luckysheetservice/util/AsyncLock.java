package com.luckysheet.luckysheetservice.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName AsyncLock
 * @Description 锁
 * @Author Quyq
 * @Date 2022/7/13 9:39
 **/
@Slf4j
public class AsyncLock {

    private static final Map<String, Object> locks = new ConcurrentHashMap<>();


    public static void lock(String key) {
        Object my = new Object();
        Object o;
        do{
            o = locks.putIfAbsent(key,my );
        }while (my != o);

    }

    public static void unlock(String key) {
        locks.remove(key);
    }


}
