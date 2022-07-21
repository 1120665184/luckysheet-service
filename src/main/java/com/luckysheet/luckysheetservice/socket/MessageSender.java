package com.luckysheet.luckysheetservice.socket;

import com.alibaba.fastjson.JSON;
import com.luckysheet.luckysheetservice.socket.entity.Constant;
import com.luckysheet.luckysheetservice.socket.entity.Res;
import com.luckysheet.luckysheetservice.socket.session.LuckysheetSession;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName MessageSender
 * 消息发送
 * @Author Quyq
 * @Date 2022/7/12 17:37
 **/
@Slf4j
public class MessageSender {


    /**
     * 推送数据更改到其他session
     * @param target
     * @param data
     * @param from
     */
    public static void sendChange(Session target , String data, Session from){
        Res<String> res = new Res<>();
        res.setData(data)
                .setId(from.getId())
                .setType(Constant.RES_TYPE_SEND_OTHER)
                .setCreateTime(System.currentTimeMillis());
        send(target , res);
    }

    /**
     * 发送其他session的不同选取操作
     * @param target
     * @param data
     * @param from
     */
    public static void sendAreaInfo(Session target , String data, Session from){
        Res<String> res = new Res<>();
        res.setData(data)
                .setId(from.getId())
                .setType(Constant.RES_TYPE_AREA_INFO)
                .setCreateTime(System.currentTimeMillis());
        send(target , res);
    }

    /**
     * 回复消息
     * @param session
     * @param data
     */
    public static void replyInfo(Session session ,String data){
        Res<String> res = new Res<>();
        res.setData(data)
                .setId(session.getId())
                .setStatus(Constant.RES_STATUS_OTHER)
                .setCreateTime(System.currentTimeMillis());
        send(session , res);
    }

    /**
     * 回复连接成功消息
     * @param session
     */
    public static void replyConnect(Session session){
        Res<String> res = new Res<>();
        res.setId(session.getId())
                .setStatus(Constant.RES_STATUS_OTHER)
                .setType(Constant.RES_TYPE_CONNECT_SUCCESS)
                .setCreateTime(System.currentTimeMillis());
        send(session , res);
    }

    /**
     * 回复连接断开
     * @param target
     */
    public static void replyConnectEnd(Session target,Session from){
        Res<String> res = new Res<>();
        res.setId(from.getId())
                .setStatus(Constant.RES_STATUS_OTHER)
                .setType(Constant.RES_TYPE_CONNECT_END)
                .setMessage("用户退出")
                .setCreateTime(System.currentTimeMillis());
        send(target,res);
    }



    /**
     * 发送消息
     * @param session
     * @param res
     * @param <T>
     */
    public static <T> void send(Session session , Res<T> res){
        res.setUsername(String.format("用户_%s",res.getId()));
        try {
            session.getBasicRemote().sendText(JSON.toJSONString(res));
        }catch (IOException e){
            log.error("MessageSender send:",e);
        }
    }

}
