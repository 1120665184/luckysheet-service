package com.luckysheet.luckysheetservice.socket.filter.sync;

import com.luckysheet.luckysheetservice.socket.entity.RequestData;

/**
 * @ClassName ShareSyncData
 * @Description 共享编辑同步数据处理
 * @Author Quyq
 * @Date 2022/7/14 10:09
 **/
public interface ShareSyncData<T> {

    /**
     * 同步逻辑
     * @param data
     */
    void sync(RequestData<T> data);

}
