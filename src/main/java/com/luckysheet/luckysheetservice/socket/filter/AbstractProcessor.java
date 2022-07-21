package com.luckysheet.luckysheetservice.socket.filter;

import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;

/**
 * @ClassName AbstractProcessor
 * 统一抽象处理接口
 * @Author Quyq
 * @Date 2022/7/12 20:21
 **/
@Slf4j
public abstract class AbstractProcessor implements Processor{
    @Override
    public void process(RequestData<?> request, Session from, ProcessorChain chain) {
        boolean flag = this.doProcess(request, from);
        if(!flag) return;
        chain.process(request,from);
        this.mopUp(request,from);
    }

    protected abstract boolean doProcess(RequestData<?> request, Session from);

    protected void mopUp(RequestData<?> request, Session from){
        //log.info("{} 扫尾工作触发",this.getClass().toString());
    }

}
