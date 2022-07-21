package com.luckysheet.luckysheetservice.socket.filter;

import com.luckysheet.luckysheetservice.socket.entity.RequestData;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ProcessorChain
 * 处理链
 * @Author Quyq
 * @Date 2022/7/12 20:01
 **/
public class ProcessorChain {

    private final List<Processor> processors = new ArrayList<>();
    int index = 0;

    public ProcessorChain addProcessor(Processor processor){
        this.processors.add(processor);
        return this;
    }

    public ProcessorChain addProcessors(List<Processor> processors){
        this.processors.addAll(processors);
        return this;
    }

    public void process(RequestData<?> request, Session from){
        if(index == this.processors.size()) return;

        this.processors.get(index++).process(request,from,this);
    }
}
