package com.luckysheet.luckysheetservice.socket.filter;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ProcessorChainManager
 * @Description 责任链处理
 * @Author Quyq
 * @Date 2022/7/12 20:43
 **/
@Component
public class ProcessorChainManager implements ApplicationContextAware {

    private final List<Processor> allProcessor = new ArrayList<>();

    /**
     * 获取处理链
     * @return
     */
    public ProcessorChain getChain(){
        return new ProcessorChain().addProcessors(allProcessor);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Processor> beans = applicationContext.getBeansOfType(Processor.class);
        List<Processor> noOrder ;
        if(!beans.isEmpty()){
            ArrayList<Processor> allPro = new ArrayList<>(beans.values());
            noOrder = allPro.stream()
                    .filter(v -> Objects.isNull(v.getClass().getAnnotation(Order.class)))
                    .collect(Collectors.toList());
            allPro.removeAll(noOrder);
            //Order 值越小执行优先级越高
            allPro.sort((v1, v2) -> {
                int ord1 = v1.getClass().getAnnotation(Order.class).value();
                int ord2 = v2.getClass().getAnnotation(Order.class).value();
                if (ord1 == ord2)
                    return 0;
                return ord1 < ord2 ? -1 : 1;
            });
            this.allProcessor.addAll(allPro);
            this.allProcessor.addAll(noOrder);
        }
    }
}
