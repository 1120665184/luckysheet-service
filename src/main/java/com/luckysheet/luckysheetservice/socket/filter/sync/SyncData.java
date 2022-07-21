package com.luckysheet.luckysheetservice.socket.filter.sync;

import com.luckysheet.luckysheetservice.socket.enums.OperationType;

import java.lang.annotation.*;

/**
 * @ClassName SyncData
 * @Description 共享编辑数据同步类型标识
 * @Author Quyq
 * @Date 2022/7/14 10:02
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SyncData {

    OperationType value();

}
