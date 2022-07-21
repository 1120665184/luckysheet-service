package com.luckysheet.luckysheetservice.socket.filter.impl;

import com.alibaba.fastjson.JSON;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.excep.LuckySheetSyncDataException;
import com.luckysheet.luckysheetservice.socket.filter.AbstractProcessor;
import com.luckysheet.luckysheetservice.socket.filter.sync.ShareSyncData;
import com.luckysheet.luckysheetservice.socket.filter.sync.SyncData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @ClassName SyncDataProcessor
 * @Description 同步数据到指定工作簿
 * @Author Quyq
 * @Date 2022/7/12 20:35
 **/
@Order(2)
@Slf4j
@Component
public class SyncDataProcessor extends AbstractProcessor {

    @Resource
    private List<ShareSyncData> syncData;

    @Override
    protected boolean doProcess(RequestData<?> request, Session from) {

        Optional<ShareSyncData> processor = syncData.stream()
                .filter(v -> v.getClass().getAnnotation(SyncData.class).value() == request.getT())
                .findFirst();

        if (processor.isPresent()){
            try{
                request.setSessionId(from.getId());
                processor.get().sync(request);
            }catch (LuckySheetSyncDataException e){
                log.error(e.getMessage());
                return false;
            }

        }

        return true;
    }
}
