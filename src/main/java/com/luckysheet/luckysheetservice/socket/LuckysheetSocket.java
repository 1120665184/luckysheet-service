package com.luckysheet.luckysheetservice.socket;

import com.luckysheet.luckysheetservice.socket.decoder.RequestDecoder;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.Map;

/**
 * @ClassName LuckysheetSocket
 * luckysheet socket处理
 * @Author Quyq
 * @Date 2022/7/12 14:55
 **/
@ServerEndpoint(value = "/ws/lucksheet",decoders = {RequestDecoder.class})
@Component
public class LuckysheetSocket {

    private static LuckysheetImpl impl;

    @Resource
    public void setImpl(LuckysheetImpl sheetImpl){
        impl = sheetImpl;
    }


    @OnOpen
    public void onOpen(Session session){
        String gridKey = getGridKey(session);
        impl.open(session , gridKey);
    }

    @OnClose
    public void onClose(Session session){
        String gridKey = getGridKey(session);
        impl.close(session , gridKey);
    }

    @OnMessage
    public void onMessage(RequestData<?> message , Session session){
        message.setGridKey(getGridKey(session));
        impl.message(message , session);
    }

    @OnError
    public void onError(Session session , Throwable error){
        String gridKey = getGridKey(session);
        impl.error(session , error , gridKey);
    }


    private String getGridKey(Session session){
        Map<String, List<String>> map = session.getRequestParameterMap();
        return String.join(",",map.get("g"));
    }

}
