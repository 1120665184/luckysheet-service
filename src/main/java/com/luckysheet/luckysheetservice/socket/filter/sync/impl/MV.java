package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.alibaba.fastjson.JSONArray;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import com.luckysheet.luckysheetservice.socket.session.SessionManager;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * @ClassName MV
 * @Description 移动位置处理
 * @Author Quyq
 * @Date 2022/7/15 16:16
 **/
@Component
@SyncData(OperationType.mv)
public class MV implements ShareSyncData<Object> {
    @Override
    public void sync(RequestData<Object> data) {
        if(Objects.isNull(data.getV())) return;

        if(data.getV() instanceof JSONArray){
            JSONArray v = (JSONArray)data.getV();
            Map<String, Object> otherInfo = SessionManager.getSession(data.getGridKey(), data.getSessionId())
                    .getOtherInfo();
            otherInfo.put("sheet_index",data.getI());
            otherInfo.put("sheet_top",v.getJSONObject(0).get("top"));
            otherInfo.put("sheet_left",v.getJSONObject(0).get("left"));
        }
    }
}
