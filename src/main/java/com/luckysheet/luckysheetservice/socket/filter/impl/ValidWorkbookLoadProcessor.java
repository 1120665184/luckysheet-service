package com.luckysheet.luckysheetservice.socket.filter.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.entity.LuckyWorkbook;
import com.luckysheet.luckysheetservice.service.ILuckySheetService;
import com.luckysheet.luckysheetservice.service.ILuckyWorkbookService;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.filter.AbstractProcessor;
import com.luckysheet.luckysheetservice.util.AsyncLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName ValidWorkbookLoadProcessor
 * @Description 校验请求中的工作簿是否已加载到缓存
 * @Author Quyq
 * @Date 2022/7/12 20:32
 **/
@Order(1)
@Slf4j
@Component
public class ValidWorkbookLoadProcessor extends AbstractProcessor {

    @Resource private ILuckyWorkbookService workbookService;

    @Resource private ILuckySheetService sheetService;


    @Override
    protected boolean doProcess(RequestData<?> request, Session from) {

        log.debug("命令请求：{}", JSON.toJSONString(request));

        String gridKey = request.getGridKey();
        String index = request.getI();
        //特殊操作类型需要校验的sheet
        if(request.getT() == OperationType.shc && Objects.nonNull(request.getV())){
            JSONObject shcV =(JSONObject)request.getV();
            index = shcV.getString("copyindex");
        }else if(request.getT() == OperationType.shs){
            index = (String) request.getV();
        } else if(request.getT() == OperationType.thumb){
            index = request.getCurindex();
        }
        //校验缓存中是否有该工作簿信息
        if(!CacheManager.hasWorkbookInfo(gridKey)){

            //加载工作簿信息
            LuckyWorkbook info = workbookService.getById(gridKey);
            if(Objects.isNull(info)){
                log.error("未找到该工作簿：{}",gridKey);
                return false;
            }

            //默认将status == 1 的sheet加载到缓存中
            LambdaQueryWrapper<LuckySheet> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(LuckySheet::getIndex)
                    .eq(LuckySheet::getGridKey , gridKey)
                    .eq(LuckySheet::getStatus,1);
            String statusIndex = sheetService.getOne(wrapper, false).getIndex();

            List<LuckySheet> statusSheet = workbookService.getSheetAndAllCells(gridKey, Collections.singletonList(statusIndex));
            try{
                AsyncLock.lock(gridKey);
                if(!CacheManager.hasWorkbookInfo(gridKey)){
                    //加载到缓存
                    log.info("{} 工作簿和激活sheet页放入缓存中",info.getGridKey());
                    CacheManager.put(info);
                    CacheManager.put(gridKey , statusSheet.get(0));
                }
            }finally {
                AsyncLock.unlock(gridKey);
            }

        }

        //校验缓存中的工作簿中是否有该sheet页数据
        if(Objects.nonNull(index)  && !CacheManager.hasSheet(gridKey, index)
                && !CacheManager.isDeleteSheet(gridKey,index)){
            String lockKey = String.format("%s:%s",gridKey, index);
            try{

                AsyncLock.lock(lockKey);

                //加载该工作簿中的指定sheet数据
                List<LuckySheet> sheet = workbookService.getSheetAndAllCells(gridKey, Collections.singletonList(index));
                if(CollectionUtils.isEmpty(sheet)){
                    log.warn("未找到 {} 工作簿的 {} sheet页",gridKey, index);
                    return false;
                }

                if(!CacheManager.hasSheet(gridKey, index) && !CacheManager.isDeleteSheet(gridKey,index)){

                    if(!CacheManager.put(gridKey,sheet.get(0))){
                        log.error("{} 工作簿的 {} sheet页入缓存异常！",gridKey, index);
                        return false;
                    }
                }
            }finally {
                AsyncLock.unlock(lockKey);
            }
        }

        return true;
    }
}
