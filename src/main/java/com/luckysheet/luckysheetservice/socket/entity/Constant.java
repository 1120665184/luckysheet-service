package com.luckysheet.luckysheetservice.socket.entity;

/**
 * @ClassName Constant
 * @Description TODO
 * @Author Quyq
 * @Date 2022/7/12 9:40
 **/
public interface Constant {

    String RES_STATUS_CHANGE = "0";

    String RES_STATUS_OTHER = "1";

    /**
     * 连接成功
     */
    Integer RES_TYPE_CONNECT_SUCCESS = 0;

    /**
     * 发送给当前连接的用户
     */
    Integer RES_TYPE_SEND = 1;

    /**
     * 发送信息给其他用户
     */
    Integer RES_TYPE_SEND_OTHER = 2;

    /**
     * 发送选取位置信息
     */
    Integer RES_TYPE_AREA_INFO = 3;

    /**
     * 用户连接断开
     */
    Integer RES_TYPE_CONNECT_END = 999;

}
