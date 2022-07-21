package com.luckysheet.luckysheetservice.socket.session;

import com.luckysheet.luckysheetservice.util.AsyncLock;

import javax.websocket.Session;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * @ClassName SessionManager
 * @Description session管理
 * @Author Quyq
 * @Date 2022/7/12 16:30
 **/
public class SessionManager {

    /**
     * 当前所有session
     * key: 对应的工作簿
     * value:session列表
     */
    private static final Map<String, List<LuckysheetSession>> CURRENT_SESSION = new ConcurrentHashMap<>();

    private static final LongAdder count = new LongAdder();

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    private static final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();


    /**
     * 获取所有的session数量
     * @return
     */
    public static long getCount(){
        return count.sum();
    }

    /**
     * 获取所有session拷贝
     * 注意：缺少实际的session实例
     * @param gridKey
     * @return
     */
    public static void getSession(String gridKey, Consumer<LuckysheetSession> func){
        readLock.lock();
        try{
            Optional.ofNullable(CURRENT_SESSION.get(gridKey))
                    .ifPresent(ss ->{
                        if(Objects.nonNull(func)){
                            for (LuckysheetSession s : ss)
                                func.accept(s);
                        }
                    });
        }finally {
            readLock.unlock();
        }
    }

    /**
     * 获取指定session实例
     * @param gridKey
     * @param sessionId
     * @return
     */
    public static LuckysheetSession getSession(String gridKey , String sessionId){
        LuckysheetSession session = null;
        readLock.lock();
        try {
            Optional<List<LuckysheetSession>> sesstionOptions = Optional.ofNullable(CURRENT_SESSION.get(gridKey));
            if(sesstionOptions.isPresent()){
                session = sesstionOptions.get().stream().filter(s -> s.getId().equals(sessionId))
                        .findFirst().orElse(null);
            }
        }finally {
            readLock.unlock();
        }
        return session;
    }

    /**
     * 注册
     * @param gridKey
     * @param session
     */
    public static void login(String gridKey, Session session){
        writeLock.lock();
        try{
            Optional.ofNullable(CURRENT_SESSION.get(gridKey))
                    .orElseGet(() -> {
                        ArrayList<LuckysheetSession> v = new ArrayList<>();
                        CURRENT_SESSION.put(gridKey, v);
                        return v;
                    }).add(
                    LuckysheetSession.builder()
                            .gridKey(gridKey)
                            .id(session.getId())
                            .session(session)
                            .createTime(LocalDateTime.now())
                            .otherInfo(new HashMap<>())
                            .build()
            );
        }finally {
            writeLock.unlock();
        }
        count.increment();
    }

    /**
     * 注销
     * @param gridKey
     * @param sessionId
     */
    public static void logout(String gridKey , String sessionId){
        writeLock.lock();
        try{
            Optional.ofNullable(CURRENT_SESSION.get(gridKey))
                    .ifPresent(v -> {
                        for (int i = 0 ; i < v.size() ; i ++){
                            LuckysheetSession session = v.get(i);
                            if(session.getId().equals(sessionId)){
                                v.remove(i);
                                break;
                            }
                        }
                        if(v.isEmpty())
                            CURRENT_SESSION.remove(gridKey);
                    });
        }finally {
            writeLock.unlock();
        }
        count.decrement();
    }

}
