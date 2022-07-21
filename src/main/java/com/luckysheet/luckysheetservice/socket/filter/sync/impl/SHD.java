package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.alibaba.fastjson.JSONObject;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import com.luckysheet.luckysheetservice.util.AsyncLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName SHD
 * @Description 删除sheet
 * @Author Quyq
 * @Date 2022/7/14 20:24
 **/
@Component
@SyncData(OperationType.shd)
@Slf4j
public class SHD implements ShareSyncData<JSONObject> {
    @Override
    public void sync(RequestData<JSONObject> data) {
        String index = data.getV().getString("deleIndex");
        String lockKey = String.format("%s:%s",data.getGridKey(), index);
        AsyncLock.lock(lockKey);
        try{
            CacheManager.deleteSheet(data.getGridKey(),index);
        }finally {
            AsyncLock.unlock(lockKey);
        }

        log.warn("{} 工作簿中的 {} sheet页被删除",data.getGridKey() , index);
    }
}
