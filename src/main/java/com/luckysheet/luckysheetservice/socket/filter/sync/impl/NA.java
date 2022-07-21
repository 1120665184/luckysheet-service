package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @ClassName NA
 * @Description 修改工作簿名称
 * @Author Quyq
 * @Date 2022/7/15 14:45
 **/
@Component
@SyncData(OperationType.na)
public class NA implements ShareSyncData<String> {

    @Override
    public void sync(RequestData<String> data) {
        if(!StringUtils.hasText(data.getV())) return;

        CacheManager.getWorkbook(data.getGridKey())
                .setTitle(data.getV());
    }
}
