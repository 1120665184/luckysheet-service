package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.alibaba.fastjson.JSONObject;
import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.service.ILuckySheetService;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName SHA
 * @Description 新建sheet
 * @Author Quyq
 * @Date 2022/7/14 19:27
 **/
@Component
@SyncData(OperationType.sha)
@Slf4j
public class SHA implements ShareSyncData<JSONObject> {

    @Resource private ILuckySheetService sheetService;

    ExecutorService threadPool = Executors.newFixedThreadPool(2);

    @Override
    public void sync(RequestData<JSONObject> data) {
        LuckySheet luckySheet = data.getV().toJavaObject(LuckySheet.class);
        luckySheet
                .setSheetId(UUID.randomUUID().toString().replaceAll("-",""))
                .setGridKey(data.getGridKey())
                .setCelldata(new ArrayList<>());
        CacheManager.put(data.getGridKey() , luckySheet);

        threadPool.submit(() -> {
            sheetService.save(luckySheet);
        });
    }
}
