package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import com.luckysheet.luckysheetservice.util.AsyncLock;
import org.springframework.stereotype.Component;

/**
 * @ClassName FSC
 * @Description 清除筛选
 * @Author Quyq
 * @Date 2022/7/14 18:33
 **/
@Component
@SyncData(OperationType.fsc)
public class FSC implements ShareSyncData<Object> {
    @Override
    public void sync(RequestData<Object> data) {
        LuckySheet sheetInfo = CacheManager.getSheetInfo(data.getGridKey(), data.getI());

        sheetInfo.setFilter(null)
                .setFilter_select(null);

    }
}
