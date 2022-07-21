package com.luckysheet.luckysheetservice.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 工作簿->sheet页设置
 * </p>
 *
 * @author quyq
 * @since 2022-07-11
 */
@TableName("lucky_sheet")
@Data
@Accessors(chain = true)
public class LuckySheet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String sheetId;

    /**
     * 工作簿id
     */
    private String gridKey;

    /**
     * 激活状态
     */
    @TableField("`status`")
    private Integer status;

    /**
     * 工作表索引
     */
    @TableField("`index`")
    private String index;

    /**
     * 工作表名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 颜色
     */
    private String color;

    /**
     * 工作表下标
     */
    @TableField("`order`")
    private Integer order;

    /**
     * 是否隐藏
     */
    private Integer hide;

    /**
     * 行数
     */
    @TableField("`row`")
    private Integer row;

    /**
     * 列数
     */
    @TableField("`column`")
    private Integer column;

    /**
     * 自定义行高
     */
    private Integer defaultRowHeight;

    /**
     * 自定义列宽
     */
    private Integer defaultColWidth;

    /**
     * 配置项
     */
    private JSONObject config;

    /**
     * 左右滚动条位置
     */
    private String scrollLeft;

    /**
     * 上下滚动条位置
     */
    private String scrollTop;

    /**
     * 选中的区域
     */
    @TableField("luckysheet_select_save")
    private JSONArray luckysheet_select_save;

    /**
     * 公式链
     */
    private JSONArray calcChain;

    /**
     * 是否数据透视表
     */
    private Boolean isPivotTable;

    /**
     * 数据透视表设置
     */
    private JSONObject pivotTable;

    /**
     * 筛选范围
     */
    @TableField("filter_select")
    private JSONObject filter_select;

    /**
     * 筛选配置
     */
    @TableField("`filter`")
    private JSONObject filter;

    /**
     * 交替颜色
     */
    @TableField("la_save")
    private JSONArray luckysheet_alternateformat_save;

    /**
     * 自定义交替颜色
     */
    @TableField("la_save_model_custom")
    private JSONArray luckysheet_alternateformat_save_modelCustom;

    /**
     * 条件格式
     */
    @TableField("lc_save")
    private JSONArray luckysheet_conditionformat_save;

    /**
     * 冻结行列配置
     */
    private JSONObject frozen;

    /**
     * 图表配置
     */
    private JSONArray chart;

    /**
     * 缩放比例
     */
    private Integer zoomRatio;

    /**
     * 图片
     */
    private JSONObject images;

    /**
     * 是否显示网格线
     */
    private Integer showGridLines;

    /**
     * 数据验证配置
     */
    private JSONObject dataVerification;

    /**
     * 超链接
     */
    private JSONObject hyperlink;

    /**
     * cell数据
     */
    @TableField(exist = false)
    private List<LuckySheetCell> celldata;

    public LuckySheetCell findCell(Integer r ,Integer c){
        if(CollectionUtils.isEmpty(celldata)) return null;

        return celldata.stream().filter(v -> v.getR().equals(r) && v.getC().equals(c))
                .findFirst().orElse(null);
    }


    public Boolean getIsPivotTable() {
        return isPivotTable;
    }

    public void setPivotTable(JSONObject pivotTable) {
        this.pivotTable = pivotTable;
    }

    public JSONObject getPivotTable() {
        return this.pivotTable;
    }

    public void setIsPivotTable(Boolean pivotTable) {
        isPivotTable = pivotTable;
    }
}
