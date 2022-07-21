package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.service.ILuckyWorkbookService;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.excep.LuckySheetSyncDataException;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import com.luckysheet.luckysheetservice.util.AsyncLock;
import org.apache.ibatis.cache.Cache;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName SH
 * @Description sheet属性(隐藏或显示)
 * @Author Quyq
 * @Date 2022/7/15 10:50
 **/
@Component
@SyncData(OperationType.sh)
public class SH implements ShareSyncData<Integer> {

    @Resource
    private ILuckyWorkbookService workbookService;

    @Override
    public void sync(RequestData<Integer> data) {
        String op = data.getOp();
        String statusIndex = data.getCur();

        if(!StringUtils.hasText(op)) return;

        LuckySheet sheetInfo = CacheManager.getSheetInfo(data.getGridKey(), data.getI());

        //隐藏
        if("hide".equals(op)){
            sheetInfo.setStatus(0)
                    .setHide(Objects.isNull(data.getV()) ? 0 : data.getV());

            if(StringUtils.hasText(statusIndex)){

                LuckySheet statusSheet = CacheManager.getSheetInfo(data.getGridKey(), statusIndex);

                if(Objects.isNull(statusSheet)){

                    List<LuckySheet> sheet = workbookService.getSheetAndAllCells(data.getGridKey(), Collections.singletonList(statusIndex));

                    if(CollectionUtils.isEmpty(sheet)){
                        throw new RuntimeException("未找到需要设置为默认SHEET的数据！！");
                    }

                    String lockKey = String.format("%s:%s",data.getGridKey() , statusIndex);
                    AsyncLock.lock(lockKey);
                    try{
                        if(!CacheManager.hasSheet(data.getGridKey() , statusIndex)){
                            CacheManager.put(data.getGridKey() , statusSheet = sheet.get(0));
                        }
                    }finally {
                        AsyncLock.unlock(lockKey);
                    }

                }
                statusSheet.setStatus(1);

            }

        }
        else if("show".equals(op)) {
            sheetInfo.setStatus(0)
                    .setHide(0);
            //显示的时候更改为默认页
            if(!CacheManager.changeStatusTo(data.getGridKey(), data.getI()))
                throw new LuckySheetSyncDataException(data.getGridKey(), data.getI(),"status");
        }

    }
}
