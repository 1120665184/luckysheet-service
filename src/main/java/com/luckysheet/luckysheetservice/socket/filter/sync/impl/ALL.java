package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.excep.LuckySheetSyncDataException;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import com.luckysheet.luckysheetservice.util.AsyncLock;
import com.luckysheet.luckysheetservice.util.ReflectUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @ClassName ALL
 * @Description 通用保存
 * @Author Quyq
 * @Date 2022/7/14 15:01
 **/
@Component
@SyncData(OperationType.all)
public class ALL implements ShareSyncData<Object> {
    @Override
    public void sync(RequestData<Object> data) {

        if(Objects.isNull(data.getK())) return;

        String gridKey = data.getGridKey();
        String index = data.getI();

        LuckySheet sheetInfo = CacheManager.getSheetInfo(gridKey, index);

        String lockKey = String.format("%s:%s:%s",gridKey,index,data.getK());

        AsyncLock.lock(lockKey);
        try{
            if(!ReflectUtils.setFieldValue(sheetInfo,data.getK(),data.getV()))
                throw new LuckySheetSyncDataException(gridKey,index,data.getK());
        }finally {
            AsyncLock.unlock(lockKey);
        }


    }
}
