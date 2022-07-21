package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.alibaba.fastjson.JSONObject;
import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import org.springframework.stereotype.Component;

/**
 * @ClassName FSR
 * @Description 恢复筛选
 * @Author Quyq
 * @Date 2022/7/14 18:37
 **/
@Component
@SyncData(OperationType.fsr)
public class FSR implements ShareSyncData<JSONObject> {
    @Override
    public void sync(RequestData<JSONObject> data) {
        LuckySheet sheetInfo = CacheManager.getSheetInfo(data.getGridKey(), data.getI());
        sheetInfo.setFilter(data.getV().getJSONObject("filter"))
                .setFilter_select(data.getV().getJSONObject("filter_select"));
    }
}
