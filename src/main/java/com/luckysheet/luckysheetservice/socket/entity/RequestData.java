package com.luckysheet.luckysheetservice.socket.entity;

import com.alibaba.fastjson.JSONObject;
import com.luckysheet.luckysheetservice.socket.enums.OperationType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @ClassName RequestData
 * @Description socket操作请求信息
 * @Author Quyq
 * @Date 2022/7/12 9:53
 **/
@Data
@Accessors(chain = true)
public class RequestData<T> {

    /**
     * 操作类型表示符号
     */
    private OperationType t;

    /**
     * 对应的gridKey
     */
    private String gridKey;

    /**
     * sessionId
     */
    private String sessionId;

    /**
     * 当前sheet的index值
     */
    private String i;

    /**
     * 需要更新value值
     */
    private T v;
    /**
     * 行
     */
    private Integer r;
    /**
     * 列
     */
    private Integer c;

    /**
     * 范围行列数
     */
    private JSONObject range;

    /**
     * 操作的key值
     */
    private String k;

    /**
     * 操作类型
     */
    private String op;

    /**
     * 更新或者删除的函数位置
     */
    private Object pos;

    /**
     * 行操作还是列操作，值`r`代表行，`c`代表列
     */
    private String rc;

    /**
     * 隐藏后设置索引对应cur的sheet为激活状态
     */
    private String cur;

    /**
     * 接收时间
     */
    private long receiptTime;

    /**
     * 缩略图,为base64字符串
     */
    private String img;

    /**
     * 当前表格默认打开的sheet
     */
    private String curindex;

}
