package com.luckysheet.luckysheetservice.socket.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName Res
 * @Description TODO
 * @Author Quyq
 * @Date 2022/7/12 9:37
 **/
@Data
@Accessors(chain = true)
public class Res<DATA> {

    private long createTime;

    private DATA data;

    private String id;

    private String message = "success";

    private String status = Constant.RES_STATUS_CHANGE;

    private Integer type = Constant.RES_TYPE_SEND;

    private String username;

}
