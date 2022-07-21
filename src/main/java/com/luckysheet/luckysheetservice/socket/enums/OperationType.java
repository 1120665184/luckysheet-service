package com.luckysheet.luckysheetservice.socket.enums;

/**
 * @ClassName OperationType
 * @Description luckysheet所有操作类型
 * @Author Quyq
 * @Date 2022/7/12 9:50
 **/
public enum OperationType {
    /**
     * 单个单元格刷新
     */
    v,
    /**
     * 范围单元格刷新
     */
    rv,
    rv_end,
    /**
     * config操作
     */
    cg,
    /**
     * 通用保存
     */
    all,
    /**
     * 选择筛选条件
     */
    f,
    /**
     * 函数链操作
     */
    fc,
    /**
     * 删除行或列
     */
    drc,
    /**
     * 增加行或列
     */
    arc,
    /**
     * 清除筛选
     */
    fsc,
    /**
     * 恢复筛选
     */
    fsr,
    /**
     * 新建sheet
     */
    sha,
    /**
     * 复制sheet
     */
    shc,
    /**
     * 删除sheet
     */
    shd,
    /**
     * 删除sheet后恢复操作
     */
    shre,
    /**
     * 调整sheet位置
     */
    shr,
    /**
     * 切换到指定sheet
     */
    shs,
    /**
     * sheet属性(隐藏或显示)
     */
    sh,
    /**
     * 修改工作簿名称
     */
    na,
    /**
     * 缩略图
     */
    thumb,
    /**
     * 图表
     */
    c,
    /**
     * 选中
     */
    mv,
}
