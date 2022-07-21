package com.luckysheet.luckysheetservice.form;

import lombok.Data;

/**
 * @ClassName WorkbookForm
 * @Description TODO
 * @Author Quyq
 * @Date 2022/7/11 11:37
 **/
@Data
public class WorkbookForm {

    private String title;

    private Integer pageSize;

    private Integer pageNumber;

}
