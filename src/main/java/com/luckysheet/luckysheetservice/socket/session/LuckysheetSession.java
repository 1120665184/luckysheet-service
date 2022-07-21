package com.luckysheet.luckysheetservice.socket.session;

import lombok.Builder;
import lombok.Data;

import javax.websocket.Session;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName LuckysheetSession
 * @Description session基本数据
 * @Author Quyq
 * @Date 2022/7/12 16:32
 **/
@Data
@Builder
public class LuckysheetSession {

    private String id;

    private String gridKey;

    private Session session;

    /**
     * 连接时间
     */
    private LocalDateTime createTime;
    /**
     * 心跳时间
     */
    private LocalDateTime heartBeatTime ;

    /**
     * 其他信息
     */
    private Map<String,Object> otherInfo;

}
