package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.alibaba.fastjson.JSONObject;
import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.service.ILuckyWorkbookService;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import com.luckysheet.luckysheetservice.util.AsyncLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName SHRE
 * @Description 删除sheet后恢复操作
 * @Author Quyq
 * @Date 2022/7/14 21:12
 **/
@Component
@SyncData(OperationType.shre)
@Slf4j
public class SHRE implements ShareSyncData<JSONObject> {

    @Resource
    private ILuckyWorkbookService workbookService;

    @Override
    public void sync(RequestData<JSONObject> data) {
        String reIndex = data.getV().getString("reIndex");

        String gridKey = data.getGridKey();

        String lockKey = String.format("%s:%s", gridKey, reIndex);

        //将删除的数据重新如缓存
        List<LuckySheet> sheet = workbookService.getSheetAndAllCells(gridKey, Collections.singletonList(reIndex));
        if (CollectionUtils.isEmpty(sheet)) {
            throw new RuntimeException(String.format("未找到 %s 工作簿要恢复的 %s sheet页", gridKey, reIndex));
        }

        AsyncLock.lock(lockKey);
        try {
            if (!CacheManager.put(gridKey, sheet.get(0))) {
                throw new RuntimeException(String.format("%s 工作簿要恢复的 %s sheet页入缓存异常！", gridKey, reIndex));
            }

        } finally {
            AsyncLock.unlock(lockKey);
        }


    }
}
