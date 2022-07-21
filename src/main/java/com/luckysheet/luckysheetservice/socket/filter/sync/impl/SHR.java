package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.service.ILuckySheetService;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import com.luckysheet.luckysheetservice.util.AsyncLock;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @ClassName SHR
 * @Description 调整sheet位置
 * @Author Quyq
 * @Date 2022/7/15 9:23
 **/
@Component
@SyncData(OperationType.shr)
public class SHR implements ShareSyncData<JSONObject> {

    @Resource private ILuckySheetService sheetService;

    @Override
    public void sync(RequestData<JSONObject> data) {
        JSONObject shr = data.getV();


        shr.forEach((k,v) ->{
            String lockKey = String.format("%s:%s",data.getGridKey() ,k);
            AsyncLock.lock(lockKey);
            try{
                LuckySheet sheetInfo = CacheManager.getSheetInfo(data.getGridKey(), k);
                if(Objects.nonNull(sheetInfo)){
                    sheetInfo.setOrder((Integer) v);
                }else {
                    LambdaUpdateWrapper<LuckySheet> wrapper = new LambdaUpdateWrapper<>();
                    wrapper.eq(LuckySheet::getGridKey , data.getGridKey())
                            .eq(LuckySheet::getIndex ,k)
                            .set(LuckySheet::getOrder , v);
                    sheetService.update(wrapper);
                }

            }finally {
                AsyncLock.unlock(lockKey);
            }
        });
    }
}
