package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import com.luckysheet.luckysheetservice.util.AsyncLock;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @ClassName CG
 * @Description config操作
 * @Author Quyq
 * @Date 2022/7/14 14:40
 **/
@Component
@SyncData(OperationType.cg)
public class CG implements ShareSyncData<Object> {
    @Override
    public void sync(RequestData<Object> data) {

        if(Objects.isNull(data.getK()) || Objects.isNull(data.getV()))
            return;

        String gridKey = data.getGridKey();
        String index = data.getI();
        LuckySheet sheetInfo = CacheManager.getSheetInfo(gridKey, index);
        JSONObject config = sheetInfo.getConfig();
        String lockKey = String.format("%s:%s:%s",gridKey,index,"config");
        //config配置为空时设置新值
        if(Objects.isNull(config)){

            AsyncLock.lock(lockKey);
            try{
                if(Objects.isNull(config = sheetInfo.getConfig())){
                    JSONObject newConfig = new JSONObject();
                    sheetInfo.setConfig(newConfig);
                    config = newConfig;
                }
            }finally {
                AsyncLock.unlock(lockKey);
            }
        }

        String paramLockKey = String.format("%s:%s",lockKey,data.getK());
        AsyncLock.lock(paramLockKey);
        try {
            config.put(data.getK(),data.getV());
        }finally {
            AsyncLock.unlock(paramLockKey);
        }


    }
}
