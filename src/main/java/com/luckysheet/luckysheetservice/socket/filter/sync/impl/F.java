package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import com.luckysheet.luckysheetservice.util.AsyncLock;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @ClassName F
 * @Description 选择筛选条件
 * @Author Quyq
 * @Date 2022/7/15 15:04
 **/
@Component
@SyncData(OperationType.f)
public class F implements ShareSyncData<Object> {
    @Override
    public void sync(RequestData<Object> data) {
        String op = data.getOp();
        String pos = (String) data.getPos();
        if(!StringUtils.hasText(op) || !StringUtils.hasText(pos)) return;


        LuckySheet sheetInfo = CacheManager.getSheetInfo(data.getGridKey(), data.getI());
        JSONObject filter = sheetInfo.getFilter();

        if(Objects.isNull(filter)){
            String lockKey = String.format("%s:%s:%s",data.getGridKey() , data.getI() , "filter");
            AsyncLock.lock(lockKey);
            try{
                JSONObject newObj = new JSONObject();
                if(Objects.isNull(filter = sheetInfo.getFilter())){
                    sheetInfo.setFilter(filter = newObj);
                }
            }finally {
                AsyncLock.unlock(lockKey);
            }
        }

        if("upOrAdd".equals(op)){
            filter.put(pos,data);
        } else if("del".equals(op)){
            filter.remove(pos);
        }

    }
}
