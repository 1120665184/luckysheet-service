package com.luckysheet.luckysheetservice.socket.filter;

import com.luckysheet.luckysheetservice.socket.entity.RequestData;

import javax.websocket.Session;

/**
 * @ClassName Processor
 * 过滤器链处理器
 * @Author Quyq
 * @Date 2022/7/12 19:58
 **/
public interface Processor {

    /**
     * 处理逻辑
     * @param request
     * @param from
     * @param chain
     */
    void process(RequestData<?> request, Session from,ProcessorChain chain);

}
