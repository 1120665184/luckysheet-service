package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.alibaba.fastjson.JSONArray;
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

import java.util.List;
import java.util.Objects;

/**
 * @ClassName RV
 * @Description 范围单元格刷新
 * @Author Quyq
 * @Date 2022/7/14 11:23
 **/
@Component
@SyncData(OperationType.rv)
public class RV implements ShareSyncData<JSONArray> {
    @Override
    public void sync(RequestData<JSONArray> data) {
        JSONArray column = data.getRange().getJSONArray("column");
        JSONArray row = data.getRange().getJSONArray("row");
        if (Objects.isNull(column) || Objects.isNull(row) || Objects.isNull(data.getV()))
            return;

        List<Integer> c = column.toJavaList(Integer.class);
        List<Integer> r = row.toJavaList(Integer.class);

        LuckySheet sheet = CacheManager.getSheetInfo(data.getGridKey(), data.getI());
        String lockKey = String.format("%s:%s:%s",data.getGridKey(),data.getI(),"cellData");

        for (int tr = r.get(0),i = 0; i <=(r.get(1) - r.get(0)); tr++,i++) {
            for (int tc = c.get(0),j = 0;j <=(c.get(1) - c.get(0));tc++,j++){
                JSONObject v = data.getV().getJSONArray(i).getJSONObject(j);
                LuckySheetCell cacheData = sheet.findCell(tr, tc);

                //该cell没值
                if(Objects.isNull(v) || v.isEmpty()) {
                    if(Objects.nonNull(cacheData)){
                        AsyncLock.lock(lockKey);
                        try{
                            sheet.getCelldata().remove(cacheData);
                        }finally {
                            AsyncLock.unlock(lockKey);
                        }
                    }

                    continue;
                }

                //创建新cell
                if(Objects.isNull(cacheData)){
                    LuckySheetCell newCell = new LuckySheetCell();
                    newCell.setR(tr)
                            .setC(tc)
                            .setIndex(data.getI())
                            .setGridKey(data.getGridKey());
                    AsyncLock.lock(lockKey);
                    try{
                        if(Objects.isNull(cacheData = sheet.findCell(tr, tc))){
                            sheet.getCelldata().add(newCell);
                            cacheData = newCell;
                        }
                    }finally {
                        AsyncLock.unlock(lockKey);
                    }
                }
                cacheData.setV(v);


            }
        }

    }
}
