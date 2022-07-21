package com.luckysheet.luckysheetservice.socket.filter.impl;

import com.alibaba.fastjson.JSON;
import com.luckysheet.luckysheetservice.socket.MessageSender;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import com.luckysheet.luckysheetservice.socket.filter.AbstractProcessor;
import com.luckysheet.luckysheetservice.socket.session.LuckysheetSession;
import com.luckysheet.luckysheetservice.socket.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.websocket.Session;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName PushChangeToSessionProcessor
 * @Description 将更改的数据推送给其他共享用户
 * @Author Quyq
 * @Date 2022/7/12 20:40
 **/
@Order(3)
@Slf4j
@Component
public class PushChangeToSessionProcessor extends AbstractProcessor {
    @Override
    protected boolean doProcess(RequestData<?> request, Session from) {
        String message = JSON.toJSONString(request);
        //获取该工作簿共同编辑的其他人，发送更改数据。
        SessionManager.getSession(request.getGridKey(),s ->{
            if(s.getId().equals(from.getId())) return;

            //发送选中区域
            if(request.getT() == OperationType.mv){
                MessageSender.sendAreaInfo(s.getSession(),message,from);
            }
            //发送更改信息
            else {
                MessageSender.sendChange(s.getSession(), message, from);
            }

        });

        return true;
    }
}
