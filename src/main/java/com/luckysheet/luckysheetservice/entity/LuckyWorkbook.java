package com.luckysheet.luckysheetservice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 工作簿数据
 * </p>
 *
 * @author quyq
 * @since 2022-07-11
 */
@TableName("lucky_workbook")
@Data
@Accessors(chain = true)
public class LuckyWorkbook implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private String gridKey;

    /**
     * 标题
     */
    private String title;

    /**
     * 语言
     */
    private String lang;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 删除标识
     */
    private Boolean scbs;

    @TableField(exist = false)
    private List<LuckySheet> data;
}
