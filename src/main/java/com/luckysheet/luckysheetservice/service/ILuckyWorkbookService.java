package com.luckysheet.luckysheetservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.entity.LuckySheetCell;
import com.luckysheet.luckysheetservice.entity.LuckyWorkbook;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luckysheet.luckysheetservice.form.WorkbookForm;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 工作簿数据 服务类
 * </p>
 *
 * @author quyq
 * @since 2022-07-11
 */
public interface ILuckyWorkbookService extends IService<LuckyWorkbook> {

    /**
     * 通过条件分页获取
     * @param form
     * @return
     */
    IPage<LuckyWorkbook> findByCondition(WorkbookForm form);

    String insert(LuckyWorkbook book);

    String update(LuckyWorkbook book);

    /**
     * 跟新共享编辑后的数据
     * @param book
     * @param deleteSheet
     */
    void updateRealTime(LuckyWorkbook book,List<String> deleteSheet);

    void delete(List<String> gridKeys);

    void delete(String gridKeys ,List<String> index);

    /**
     * 获取sheet信息和默认cell数据
     * @param gridKey
     * @return
     */
    List<LuckySheet> getSheetAndStatusCell(String gridKey);

    /**
     * 获取指定sheet页数据
     * @param gridKey
     * @param index
     * @return
     */
    Map<String , List<LuckySheetCell>> getCells(String gridKey , List<String> index);

    /**
     * 获取指定sheet的信息和cell数据
     * @param gridKey
     * @param index
     * @return
     */
    List<LuckySheet> getSheetAndAllCells(String gridKey , List<String> index);

    @Transactional
    default String insertOrUpdate(LuckyWorkbook book){
        if(StringUtils.hasText(book.getGridKey())){
            return update(book);
        }
        return insert(book);
    }

}
