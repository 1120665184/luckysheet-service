package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.excep.LuckySheetSyncDataException;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName THUMB
 * @Description 缩略图修改
 * @Author Quyq
 * @Date 2022/7/15 14:50
 **/
@Component
@SyncData(OperationType.thumb)
public class THUMB implements ShareSyncData<Object> {
    @Override
    public void sync(RequestData<Object> data) {
        String img = data.getImg();
        String index = data.getCurindex();

        if(StringUtils.hasText(index)
                && !CacheManager.changeStatusTo(data.getGridKey() ,index))
            throw new LuckySheetSyncDataException(data.getGridKey(), data.getI(),"status");

        if(StringUtils.hasText(img)){
            byte[] imgBytes = img.getBytes(StandardCharsets.UTF_8);
            //TODO 缩略图跟新逻辑
        }





    }
}
