package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.service.ILuckySheetService;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName SHC
 * @Description 复制sheet
 * @Author Quyq
 * @Date 2022/7/14 19:47
 **/
@Component
@SyncData(OperationType.shc)
public class SHC implements ShareSyncData<JSONObject> {

    @Resource
    private ILuckySheetService sheetService;

    ExecutorService threadPool = Executors.newFixedThreadPool(2);

    @Override
    public void sync(RequestData<JSONObject> data) {
        String newIndex = data.getI();
        JSONObject v = data.getV();
        if(Objects.isNull(v) || !StringUtils.hasText(newIndex)) return;

        String copyindex = v.getString("copyindex");
        String sheetName = v.getString("name");


        LuckySheet sheetInfo = CacheManager.getSheetInfo(data.getGridKey(), copyindex);

        //深拷贝一个备份
        LuckySheet sheetCopy = JSON.parseObject(JSON.toJSONString(sheetInfo)).toJavaObject(LuckySheet.class);

        sheetCopy
                .setSheetId(UUID.randomUUID().toString().replaceAll("-",""))
                .setName(sheetName)
                .setIndex(newIndex)
                .setStatus(0);
        //将备份放入缓存
        CacheManager.put(data.getGridKey(),sheetCopy);
        threadPool.submit(() ->{
            sheetService.save(sheetCopy);
        });
    }
}
