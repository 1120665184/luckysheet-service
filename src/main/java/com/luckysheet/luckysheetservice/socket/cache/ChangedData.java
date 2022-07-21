package com.luckysheet.luckysheetservice.socket.cache;

import com.luckysheet.luckysheetservice.entity.LuckyWorkbook;

import java.util.List;

/**
 * @ClassName ChangedData
 * @Description 共享编辑最终修改后的数据,用于最后的保存操作
 * @Author Quyq
 * @Date 2022/7/14 20:35
 **/
public class ChangedData {

    private final LuckyWorkbook workbook;

    private List<String> deleteSheet;

    public ChangedData(LuckyWorkbook vals){
        this.workbook = vals;
    }

    public LuckyWorkbook getWorkbook() {
        return workbook;
    }

    public List<String> getDeleteSheet() {
        return deleteSheet;
    }

    public ChangedData setDeleteSheet(List<String> deleteSheet) {
        this.deleteSheet = deleteSheet;
        return this;
    }
}
