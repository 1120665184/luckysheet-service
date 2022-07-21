package com.luckysheet.luckysheetservice.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 工作簿 -> sheet页 -> 单元格数据
 * </p>
 *
 * @author quyq
 * @since 2022-07-11
 */
@TableName("lucky_sheet_cell")
@Data
@Accessors(chain = true)
public class LuckySheetCell implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 单元格ID
     */
    @TableId
    private String cellId;

    /**
     * 单元格行坐标
     */
    private Integer r;

    /**
     * 单元格列坐标
     */
    private Integer c;

    /**
     * 工作表标识
     */
    @TableField("`index`")
    private String index;

    /**
     * 工作簿唯一ID
     */
    private String gridKey;

    /**
     * 单元格值
     */
    private JSONObject v;

}
