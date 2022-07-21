package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.excep.LuckySheetSyncDataException;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import org.springframework.stereotype.Component;

/**
 * @ClassName SHS
 * @Description 切换到指定sheet
 * @Author Quyq
 * @Date 2022/7/15 9:50
 **/
@Component
@SyncData(OperationType.shs)
public class SHS implements ShareSyncData<String> {
    @Override
    public void sync(RequestData<String> data) {
        if(!CacheManager.changeStatusTo(data.getGridKey() , data.getV()))
            throw new LuckySheetSyncDataException(data.getGridKey(), data.getV(),"status");
    }
}
