package com.luckysheet.luckysheetservice.service.impl;

import com.luckysheet.luckysheetservice.entity.LuckySheetCell;
import com.luckysheet.luckysheetservice.mapper.LuckySheetCellMapper;
import com.luckysheet.luckysheetservice.service.ILuckySheetCellService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 工作簿 -> sheet页 -> 单元格数据 服务实现类
 * </p>
 *
 * @author quyq
 * @since 2022-07-11
 */
@Service
public class LuckySheetCellServiceImpl extends ServiceImpl<LuckySheetCellMapper, LuckySheetCell> implements ILuckySheetCellService {

}
