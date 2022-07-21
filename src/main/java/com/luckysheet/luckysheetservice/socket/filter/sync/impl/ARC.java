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
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @ClassName ARC
 * @Description 增加行或列
 * @Author Quyq
 * @Date 2022/7/14 17:13
 **/
@Component
@SyncData(OperationType.arc)
public class ARC implements ShareSyncData<JSONObject> {
    @Override
    public void sync(RequestData<JSONObject> data) {

        String rc = data.getRc();
        JSONObject v = data.getV();
        if (!StringUtils.hasText(rc) || Objects.isNull(v)) return;

        Integer index = v.getInteger("index");
        Integer len = v.getInteger("len");
        JSONArray newData = v.getJSONArray("data");
        String direction = v.getString("direction");

        LuckySheet sheetInfo = CacheManager.getSheetInfo(data.getGridKey(), data.getI());

        String lockKey = String.format("%s:%s:%s", data.getGridKey(), data.getI(), "cellData");

        AsyncLock.lock(lockKey);
        try {
            //修改行
            if ("r".equals(rc)) {
                sheetInfo.setRow(Objects.isNull(sheetInfo.getRow()) ? len : sheetInfo.getRow() + len);
                for (LuckySheetCell cell : sheetInfo.getCelldata()) {
                    if ("lefttop".equals(direction)) {
                        if (cell.getR() >= index)
                            cell.setR(cell.getR() + len);
                    } else {
                        if (cell.getR() > index)
                            cell.setR(cell.getR() + len);
                    }

                }
            }
            //修改列
            else if ("c".equals(rc)) {
                sheetInfo.setColumn(Objects.isNull(sheetInfo.getColumn()) ? len : sheetInfo.getColumn() + len);
                for (LuckySheetCell cell : sheetInfo.getCelldata()) {
                    if("lefttop".equals(direction)){
                        if (cell.getC() >= index)
                            cell.setC(cell.getC() + len);
                    }else {
                        if (cell.getC() > index)
                            cell.setC(cell.getC() + len);
                    }

                }
            }
            if (Objects.nonNull(newData) && !newData.isEmpty()) {
                for (int i = 0; i < newData.size(); i++) {
                    for (int j = 0; j < newData.getJSONArray(i).size(); j++) {
                        JSONObject nv = newData.getJSONArray(i).getJSONObject(j);
                        if (Objects.isNull(nv)) continue;

                        LuckySheetCell newCell = new LuckySheetCell();
                        newCell.setGridKey(data.getGridKey())
                                .setIndex(data.getI())
                                .setR(i + index)
                                .setC(j)
                                .setV(nv);
                        if ("c".equals(rc)) {
                            newCell.setR(i)
                                    .setC(j + index);
                        }
                        sheetInfo.getCelldata().add(newCell);
                    }
                }
            }
        } finally {
            AsyncLock.unlock(lockKey);
        }

    }
}
