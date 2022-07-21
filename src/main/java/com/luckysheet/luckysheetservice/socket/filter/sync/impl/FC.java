package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.alibaba.fastjson.JSONArray;
import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import com.luckysheet.luckysheetservice.util.AsyncLock;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @ClassName FC
 * @Description 函数链操作
 * @Author Quyq
 * @Date 2022/7/14 16:03
 **/
@Component
@SyncData(OperationType.fc)
public class FC implements ShareSyncData<String> {
    @Override
    public void sync(RequestData<String> data) {
        String op = data.getOp();
        Integer pos =  (Integer) data.getPos();
        if (Objects.isNull(op) || Objects.isNull(pos)) return;

        LuckySheet sheetInfo = CacheManager.getSheetInfo(data.getGridKey(), data.getI());
        JSONArray calcChain;
        String lockKey = String.format("%s:%s:%s", data.getGridKey(), data.getI(), "calcChain");
        AsyncLock.lock(lockKey);
        try {

            if (Objects.isNull(calcChain = sheetInfo.getCalcChain())) {
                JSONArray newCalcChain = new JSONArray();
                sheetInfo.setCalcChain(calcChain = newCalcChain);
            }
            //新增函数链
            if ("add".equals(op)) {
                calcChain.add(data.getV());
            }
            //跟新函数链
            else if ("update".equals(op)) {
                calcChain.add(pos , data.getV());
            }
            //删除函数链
            else if ("del".equals(op)) {
                calcChain.remove(pos);
            }
        } finally {
            AsyncLock.unlock(lockKey);
        }

    }
}
