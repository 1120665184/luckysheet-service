package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

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
 * @ClassName DRC
 * @Description 删除行或列
 * @Author Quyq
 * @Date 2022/7/14 16:27
 **/
@Component
@SyncData(OperationType.drc)
public class DRC implements ShareSyncData<JSONObject> {
    @Override
    public void sync(RequestData<JSONObject> data) {
        if(Objects.isNull(data.getRc()) || Objects.isNull(data.getV())) return;

        Integer index = data.getV().getInteger("index");
        Integer len = data.getV().getInteger("len");

        LuckySheet sheetInfo = CacheManager.getSheetInfo(data.getGridKey(), data.getI());
        List<LuckySheetCell> celldata = sheetInfo.getCelldata();
        String lockKey = String.format("%s:%s:%s",data.getGridKey(),data.getI(),"cellData");
        AsyncLock.lock(lockKey);
        try{
            //删除行
            if("r".equals(data.getRc())){
                for (int i = 0 ; i < celldata.size() ; i++){
                    LuckySheetCell cell = celldata.get(i);
                    if(cell.getR() < index) continue;
                    if(cell.getR()>=index && cell.getR() <(index + len)){
                        celldata.remove(i--);
                    }else if(cell.getR() >= index + len){
                        cell.setR(cell.getR() - len);
                    }
                }
            }
            //删除列
            else if("c".equals(data.getRc())){
                for (int i = 0 ; i < celldata.size() ; i++){
                    LuckySheetCell cell = celldata.get(i);
                    if(cell.getC() < index) continue;
                    if(cell.getC() >= index && cell.getC() < index + len){
                        celldata.remove(i--);
                    }else if (cell.getC() >= index + len){
                        cell.setC(cell.getC() - len);
                    }
                }
            }
        }finally {
            AsyncLock.unlock(lockKey);
        }

    }
}
