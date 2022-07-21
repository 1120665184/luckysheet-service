package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.entity.LuckySheetCell;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import com.luckysheet.luckysheetservice.util.AsyncLock;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @ClassName V
 * @Description 单个单元格刷新处理
 * @Author Quyq
 * @Date 2022/7/14 10:12
 **/
@SyncData(OperationType.v)
@Component
public class V implements ShareSyncData<JSONObject> {
    @Override
    public void sync(RequestData<JSONObject> data) {

        LuckySheet sheetInfo = CacheManager.getSheetInfo(data.getGridKey(), data.getI());

        LuckySheetCell cell = sheetInfo.findCell(data.getR(), data.getC());

        String lockKey = String.format("%s:%s:%s", data.getGridKey(), data.getI(), "cellData");

        if (Objects.isNull(data.getV())) {
            if (Objects.nonNull(cell)) {
                AsyncLock.lock(lockKey);
                try {
                    sheetInfo.getCelldata().remove(cell);
                } finally {
                    AsyncLock.unlock(lockKey);
                }
            }

            return;
        }

        if (Objects.isNull(cell)) {
            LuckySheetCell newCell = new LuckySheetCell();
            newCell.setR(data.getR())
                    .setC(data.getC())
                    .setIndex(data.getI())
                    .setGridKey(data.getGridKey());


            AsyncLock.lock(lockKey);
            try {
                if (Objects.isNull(cell = sheetInfo.findCell(data.getR(), data.getC()))) {
                    sheetInfo.getCelldata().add(newCell);
                    cell = newCell;
                }
            } finally {
                AsyncLock.unlock(lockKey);
            }
        }
        cell.setV(data.getV());

    }
}
