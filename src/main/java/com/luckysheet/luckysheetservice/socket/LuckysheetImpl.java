package com.luckysheet.luckysheetservice.socket;

import com.luckysheet.luckysheetservice.entity.LuckyWorkbook;
import com.luckysheet.luckysheetservice.service.ILuckyWorkbookService;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.cache.ChangedData;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.socket.filter.ProcessorChainManager;
import com.luckysheet.luckysheetservice.socket.session.LuckysheetSession;
import com.luckysheet.luckysheetservice.socket.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @ClassName LuckysheetImpl
 * @Description 具体逻辑实现
 * @Author Quyq
 * @Date 2022/7/12 17:19
 **/
@Component
@Slf4j
public class LuckysheetImpl {


    @Resource private ProcessorChainManager chainManager;

    @Resource private ILuckyWorkbookService workbookService;

    final ExecutorService threadPool = Executors.newCachedThreadPool();

    public void open(Session from,String gridKey){
        threadPool.submit(() ->{
            SessionManager.login(gridKey , from);
            MessageSender.replyConnect(from);
            log.info("session {} 注册 {} 工作簿！",from.getId() , gridKey);
        });
    }

    public void close(Session from ,String gridKey){

        threadPool.submit(() ->{
            SessionManager.logout(gridKey , from.getId());

            final AtomicBoolean hasS = new AtomicBoolean(false);

            SessionManager.getSession(gridKey,s ->{
                hasS.set(true);
                //向其他同组成员通知该session下线消息
                MessageSender.replyConnectEnd(s.getSession(),from);
            });
            if(!hasS.get()){
                //没有session信息，表明全部参与共享编辑的人都退出，保存修改后的最终数据
                ChangedData endData = CacheManager.remove(gridKey);
                if(Objects.nonNull(endData))
                    workbookService.updateRealTime(endData.getWorkbook(),endData.getDeleteSheet());
            }
            log.info("session {} 注销 {} 工作簿。",from.getId() , gridKey);
        });
    }

    public void message(RequestData<?> message , Session session){
        threadPool.submit(() -> chainManager.getChain().process(message , session));

    }

    public void error(Session session , Throwable error,String gridKey){
        log.error("程序错误：",error);
    }

}
