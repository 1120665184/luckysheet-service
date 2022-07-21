package com.luckysheet.luckysheetservice.socket.cache;

import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.entity.LuckySheetCell;
import com.luckysheet.luckysheetservice.entity.LuckyWorkbook;
import com.luckysheet.luckysheetservice.util.AsyncLock;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName WorkbookCache
 * @Description 工作簿缓存类，存储缓存信息
 * @Author Quyq
 * @Date 2022/7/13 11:00
 **/
public class WorkbookCache {

    public WorkbookCache(LuckyWorkbook baseInfo){
        //this.baseInfo = new Proxy<LuckyWorkbook>(baseInfo).getProxy();
        this.baseInfo = baseInfo;
    }

    //记录删除的sheed index值
    private final List<String> deleteSheed = new CopyOnWriteArrayList<>();

    private final LuckyWorkbook baseInfo;

    private final Map<String, LuckySheet> sheets = new ConcurrentHashMap<>();

    public LuckyWorkbook getBaseInfo(){
        return this.baseInfo;
    }

    public LuckySheet getSheet(String index){
        return sheets.get(index);
    }

    /**
     * 放入sheet数据
     * @param sheet
     */
    public void putSheet(LuckySheet sheet){
       // this.sheets.put(sheet.getIndex(),new Proxy<LuckySheet>(sheet).getProxy());
        this.sheets.put(sheet.getIndex(),sheet);
        this.deleteSheed.remove(sheet.getIndex());
    }

    public void deleteSheet(String index){
        this.sheets.remove(index);
        deleteSheed.add(index);

    }

    public boolean isDelete(String index){
        return this.deleteSheed.contains(index);
    }

    public List<String> getDeleteSheet(){
        return this.deleteSheed;
    }

    public LuckyWorkbook getFinData(){
         baseInfo.setData(new ArrayList<>(sheets.values()));
         return baseInfo;
    }

    public boolean changeStatus(String index){
        if(this.sheets.values().stream().noneMatch(v -> v.getStatus() == 1))
            return false;
        AtomicBoolean flag = new AtomicBoolean(false);
        AtomicReference<String> oldStatus = new AtomicReference<>();
        this.sheets.forEach((k,v) -> {
            if(v.getIndex().equals(index)){
                v.setStatus(1);
                flag.set(true);
            }else if(v.getStatus() == 1){
                v.setStatus(0);
                oldStatus.set(v.getIndex());
            }
        });
        if(!flag.get()){
            this.sheets.get(oldStatus.get()).setStatus(1);
            return false;
        }

        return true;
    }


    private static class Proxy<T> implements MethodInterceptor{

        private final T target;

        public Proxy(T target){
            this.target = target;
        }

        public T getTarget(){
            return this.target;
        }

        public T getProxy(){
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(this.target.getClass());
            enhancer.setCallback(this);
            return (T)enhancer.create();
        }

        @Override
        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object result = null;

            String lockKey;
            //协同编辑时，修改工作簿和sheet信息时需要上锁
            if(method.getName().startsWith("set") &&
                    StringUtils.hasText(lockKey = getLockKey(method.getName()))){
                try {
                    AsyncLock.lock(lockKey);
                    result = methodProxy.invokeSuper(o,args);
                }finally {
                    AsyncLock.unlock(lockKey);
                }
            }else {
                result = methodProxy.invokeSuper(o,args);
            }

            return result;
        }

        protected String getLockKey(String methodName){

            if(target instanceof LuckyWorkbook){
                LuckyWorkbook t = (LuckyWorkbook)target;
                return String.format("%s:%s",t.getGridKey(),methodName);
            }else if(target instanceof LuckySheet){
                LuckySheet t = (LuckySheet)target;
                return String.format("%s:%s:%s",t.getGridKey(),t.getIndex(),methodName);
            }
            return null;
        }

    }

}
