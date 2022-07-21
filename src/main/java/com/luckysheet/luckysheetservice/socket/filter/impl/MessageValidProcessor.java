package com.luckysheet.luckysheetservice.socket.filter.impl;

import com.luckysheet.luckysheetservice.socket.MessageSender;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.filter.AbstractProcessor;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName MessageValidProcessor
 * @Description 校验消息合法性及类型
 * @Author Quyq
 * @Date 2022/7/13 9:16
 **/
@Order(0)
@Component
@Slf4j
public class MessageValidProcessor extends AbstractProcessor {

    private final Map<String ,Long> recentlyData = ExpiringMap.builder()
            .maxSize(100)
            //2秒过期时间
            .expiration(2, TimeUnit.SECONDS)
            .variableExpiration()
            .expirationPolicy(ExpirationPolicy.CREATED)
            .build();

    @Override
    protected boolean doProcess(RequestData<?> request, Session from) {
        if(Objects.isNull(request.getT())){
            //心跳消息
            if("rub".equals(request.getV())){
                MessageSender.replyInfo(from , "ack");
            }
            return false;
        }
        //过滤掉短时间内推送的相同数据内容
        String d = String.format("%s&%s&%s&%s",request.getGridKey(),request.getI(),
                request.getT(),request.getV());

        Long receipt;
        if(Objects.nonNull(receipt = recentlyData.get(d)) && request.getReceiptTime() - receipt < 1000){
            return false;
        }
        recentlyData.put(d,request.getReceiptTime());
        return true;
    }
}
