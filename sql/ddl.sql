/*
 Navicat Premium Data Transfer

 Source Server         : local_docker_mysql
 Source Server Type    : MySQL
 Source Server Version : 80027
 Source Host           : 127.0.0.1:13306
 Source Schema         : luckysheet

 Target Server Type    : MySQL
 Target Server Version : 80027
 File Encoding         : 65001

 Date: 15/07/2022 17:27:09
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for lucky_sheet
-- ----------------------------
DROP TABLE IF EXISTS `lucky_sheet`;
CREATE TABLE `lucky_sheet`
(
    `sheet_id`               varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `grid_key`               varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工作簿id',
    `status`                 tinyint(1) NOT NULL COMMENT '激活状态',
    `index`                  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工作表索引',
    `name`                   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工作表名称',
    `color`                  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '颜色',
    `order`                  int(0) NULL DEFAULT NULL COMMENT '工作表下标',
    `hide`                   tinyint(1) NULL DEFAULT NULL COMMENT '是否隐藏',
    `row`                    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '行数',
    `column`                 varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '列数',
    `default_row_height`     varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '自定义行高',
    `default_col_width`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '自定义列宽',
    `config`                 json NULL COMMENT '配置项',
    `scroll_left`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '左右滚动条位置',
    `scroll_top`             varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '上下滚动条位置',
    `luckysheet_select_save` json NULL COMMENT '选中的区域',
    `calc_chain`             json NULL COMMENT '公式链',
    `is_pivot_table`         tinyint(1) NULL DEFAULT NULL COMMENT '是否数据透视表',
    `pivot_table`            json NULL COMMENT '数据透视表设置',
    `filter_select`          json NULL COMMENT '筛选范围',
    `filter`                 json NULL COMMENT '筛选配置',
    `la_save`                json NULL COMMENT '交替颜色',
    `la_save_model_custom`   json NULL COMMENT '自定义交替颜色',
    `lc_save`                json NULL COMMENT '条件格式',
    `frozen`                 json NULL COMMENT '冻结行列配置',
    `chart`                  json NULL COMMENT '图表配置',
    `zoom_ratio`             tinyint(1) NULL DEFAULT NULL COMMENT '缩放比例',
    `images`                 json NULL COMMENT '图片',
    `show_grid_lines`        tinyint(1) NULL DEFAULT NULL COMMENT '是否显示网格线',
    `data_verification`      json NULL COMMENT '数据验证配置',
    `hyperlink`              json NULL COMMENT '超链接',
    PRIMARY KEY (`sheet_id`) USING BTREE,
    INDEX                    `sheet_grid_index`(`grid_key`, `index`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '工作簿->sheet页设置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for lucky_sheet_cell
-- ----------------------------
DROP TABLE IF EXISTS `lucky_sheet_cell`;
CREATE TABLE `lucky_sheet_cell`
(
    `cell_id`  varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单元格ID',
    `r`        int(0) NULL DEFAULT NULL COMMENT '单元格行坐标',
    `c`        int(0) NULL DEFAULT NULL COMMENT '单元格列坐标',
    `index`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工作表标识',
    `grid_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '工作簿唯一ID',
    `v`        json NULL COMMENT '单元格值',
    PRIMARY KEY (`cell_id`) USING BTREE,
    INDEX      `cell_grid_index_r_c`(`grid_key`, `index`, `r`, `c`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '工作簿 -> sheet页 -> 单元格数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for lucky_workbook
-- ----------------------------
DROP TABLE IF EXISTS `lucky_workbook`;
CREATE TABLE `lucky_workbook`
(
    `grid_key`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键',
    `title`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
    `lang`        varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '语言',
    `create_time` datetime(0) NULL DEFAULT NULL,
    `update_time` datetime(0) NULL DEFAULT NULL,
    `scbs`        tinyint(1) NULL DEFAULT NULL COMMENT '删除标识',
    PRIMARY KEY (`grid_key`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '工作簿数据' ROW_FORMAT = Dynamic;

SET
FOREIGN_KEY_CHECKS = 1;
