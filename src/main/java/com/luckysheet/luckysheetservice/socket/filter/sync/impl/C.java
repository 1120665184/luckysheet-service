package com.luckysheet.luckysheetservice.socket.filter.sync.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.excep.LuckySheetSyncDataException;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import com.luckysheet.luckysheetservice.util.AsyncLock;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @ClassName C
 * @Description 图表操作
 * @Author Quyq
 * @Date 2022/7/15 15:27
 **/
@Component
@SyncData(OperationType.c)
public class C implements ShareSyncData<JSONObject> {
    @Override
    public void sync(RequestData<JSONObject> data) {
        String op = data.getOp();
        JSONObject v = data.getV();
        String gridKey = data.getGridKey();
        String index = data.getI();

        if (!StringUtils.hasText(op) || Objects.isNull(v)) return;

        String chart_id = v.getString("chart_id");
        if(!StringUtils.hasText(chart_id)) return;

        LuckySheet sheetInfo = CacheManager.getSheetInfo(gridKey, index);
        JSONArray chart = sheetInfo.getChart();
        String lockKey = String.format("%s:%s:%s", gridKey, index, "chart");

        AsyncLock.lock(lockKey);
        try {
            if (Objects.isNull(chart)) {
                JSONArray newChart = new JSONArray();
                sheetInfo.setChart(chart = newChart);
            }
            //新增操作
            if ("add".equals(op)) {
                chart.add(v);
                return;
            }

            //从图表中找出对应的图标
            JSONObject chartItem = null;
            int i = 0;
            for (; i < chart.size() ; i ++){
                chartItem = chart.getJSONObject(i);
                if(chart_id.equals(chartItem.getString("chart_id")))
                    break;
                if(i == chart.size() - 1) chartItem = null;
            }
            //未找到指定的图表
            if(Objects.isNull(chartItem))
                throw new LuckySheetSyncDataException(gridKey,index,"chart");

            //删除操作
            if("del".equals(op)){
                chart.remove(i);
                return;
            }

            //其他操作

            for (String key : v.keySet()){
                chartItem.put(key , v.get(key));
            }

        } finally {
            AsyncLock.unlock(lockKey);
        }



    }
}
