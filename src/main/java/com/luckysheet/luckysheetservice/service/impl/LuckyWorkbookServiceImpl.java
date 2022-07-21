package com.luckysheet.luckysheetservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.entity.LuckySheetCell;
import com.luckysheet.luckysheetservice.entity.LuckyWorkbook;
import com.luckysheet.luckysheetservice.form.WorkbookForm;
import com.luckysheet.luckysheetservice.mapper.LuckySheetCellMapper;
import com.luckysheet.luckysheetservice.mapper.LuckySheetMapper;
import com.luckysheet.luckysheetservice.mapper.LuckyWorkbookMapper;
import com.luckysheet.luckysheetservice.service.ILuckySheetCellService;
import com.luckysheet.luckysheetservice.service.ILuckySheetService;
import com.luckysheet.luckysheetservice.service.ILuckyWorkbookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 工作簿数据 服务实现类
 * </p>
 *
 * @author quyq
 * @since 2022-07-11
 */
@Service
public class LuckyWorkbookServiceImpl extends ServiceImpl<LuckyWorkbookMapper, LuckyWorkbook> implements ILuckyWorkbookService {


    @Resource
    private ILuckySheetService sheetService;

    @Resource
    private ILuckySheetCellService sheetCellService;

    @Override
    public IPage<LuckyWorkbook> findByCondition(WorkbookForm form) {

        Page<LuckyWorkbook> page = new Page<>(form.getPageNumber(), form.getPageSize());

        LambdaQueryWrapper<LuckyWorkbook> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(form.getTitle()), LuckyWorkbook::getTitle, form.getTitle())
                .orderByDesc(LuckyWorkbook::getUpdateTime)
                .orderByDesc(LuckyWorkbook::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public String insert(LuckyWorkbook book) {
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        book.setGridKey(id)
                .setCreateTime(LocalDateTime.now());
        baseMapper.insert(book);

        if (!CollectionUtils.isEmpty(book.getData())) {
            insertSheetAndCell(id, book.getData());
        }

        return id;
    }

    private void insertSheetAndCell(String gridKey, List<LuckySheet> sheets) {

        for (LuckySheet sheet : sheets) {
            sheet.setGridKey(gridKey)
                    .setSheetId(UUID.randomUUID().toString().replaceAll("-", ""));
            sheetService.save(sheet);
            if (!CollectionUtils.isEmpty(sheet.getCelldata())) {
                sheet.getCelldata().forEach(v -> v.setGridKey(gridKey).setIndex(sheet.getIndex())
                        .setCellId(UUID.randomUUID().toString().replaceAll("-", "")));
                sheetCellService.saveBatch(sheet.getCelldata());
            }
        }
    }


    @Override
    @Transactional
    public String update(LuckyWorkbook book) {
        book.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(book);

        if (!CollectionUtils.isEmpty(book.getData())) {
            LambdaQueryWrapper<LuckySheet> sheetWrapper = new LambdaQueryWrapper<>();
            sheetWrapper.eq(LuckySheet::getGridKey, book.getGridKey());
            sheetService.remove(sheetWrapper);

            LambdaQueryWrapper<LuckySheetCell> cellWrapper = new LambdaQueryWrapper<>();
            cellWrapper.eq(LuckySheetCell::getGridKey, book.getGridKey());
            sheetCellService.remove(cellWrapper);

            insertSheetAndCell(book.getGridKey(), book.getData());

        }

        return book.getGridKey();
    }

    @Override
    @Transactional
    public void updateRealTime(LuckyWorkbook book, List<String> deleteSheet) {
        book.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(book);
        if (!CollectionUtils.isEmpty(book.getData())) {
            //删除更改过的sheet的数据信息
            List<String> indexs = book.getData().stream().map(LuckySheet::getIndex).collect(Collectors.toList());
            LambdaQueryWrapper<LuckySheet> sheetWrapper = new LambdaQueryWrapper<>();
            sheetWrapper.eq(LuckySheet::getGridKey, book.getGridKey())
                    .in(LuckySheet::getIndex, indexs);
            sheetService.remove(sheetWrapper);

            LambdaQueryWrapper<LuckySheetCell> cellWrapper = new LambdaQueryWrapper<>();
            cellWrapper.eq(LuckySheetCell::getGridKey, book.getGridKey())
                    .in(LuckySheetCell::getIndex, indexs);
            sheetCellService.remove(cellWrapper);
            //重新插入
            insertSheetAndCell(book.getGridKey(), book.getData());
        }
        delete(book.getGridKey(), deleteSheet);
    }

    @Override
    @Transactional
    public void delete(List<String> gridKeys) {
        baseMapper.deleteBatchIds(gridKeys);
        LambdaQueryWrapper<LuckySheet> sheetWrapper = new LambdaQueryWrapper<>();
        sheetWrapper.in(LuckySheet::getGridKey, gridKeys);
        sheetService.remove(sheetWrapper);
        LambdaQueryWrapper<LuckySheetCell> cellWrapper = new LambdaQueryWrapper<>();
        cellWrapper.in(LuckySheetCell::getGridKey, gridKeys);
        sheetCellService.remove(cellWrapper);
    }

    @Override
    @Transactional
    public void delete(String gridKeys, List<String> index) {
        if (CollectionUtils.isEmpty(index)) return;

        LambdaQueryWrapper<LuckySheet> sheetWrapper = new LambdaQueryWrapper<>();
        sheetWrapper.eq(LuckySheet::getGridKey, gridKeys)
                .in(LuckySheet::getIndex, index);
        sheetService.remove(sheetWrapper);

        LambdaQueryWrapper<LuckySheetCell> cellWrapper = new LambdaQueryWrapper<>();
        cellWrapper.eq(LuckySheetCell::getGridKey, gridKeys)
                .in(LuckySheetCell::getIndex, index);
        sheetCellService.remove(cellWrapper);

    }


    @Override
    public List<LuckySheet> getSheetAndStatusCell(String gridKey) {
        List<LuckySheet> sheets = getSheet(gridKey, null);
        boolean statusSheetHasData = false;
        //将缓存中的最新数据替换到返回数据中
        for (int i = 0; i < sheets.size(); i++) {
            LuckySheet sheet = sheets.get(i);
            if (CacheManager.isDeleteSheet(gridKey, sheet.getIndex())) {
                sheets.remove(i--);
                continue;
            }
            LuckySheet newSh;
            if (Objects.nonNull(newSh = CacheManager.getSheetInfo(gridKey, sheet.getIndex()))) {
                BeanUtils.copyProperties(newSh, sheet);
                //过滤掉除了激活状态的其他sheet的数据
                if (sheet.getStatus() != 1) sheet.setCelldata(Collections.emptyList());
                else statusSheetHasData = true;
            }
        }
        if (!statusSheetHasData)
            sheets.stream().filter(v -> v.getStatus().equals(1))
                    .findFirst().ifPresent(v -> v.setCelldata(getCellsDetail(gridKey, Collections.singletonList(v.getIndex()))));
        return sheets;
    }

    @Override
    public Map<String, List<LuckySheetCell>> getCells(String gridKey, List<String> index) {

        HashMap<String, List<LuckySheetCell>> finV = new HashMap<>();

        List<String> noClioudIndex = new ArrayList<>();
        //如果缓存中存在数据，从缓存中获取最新数据
        for (String in : index) {
            if(CacheManager.isDeleteSheet(gridKey , in)) continue;

            if (!CacheManager.hasSheet(gridKey, in)) {
                noClioudIndex.add(in);
                continue;
            }

            finV.put(in, CacheManager.getSheetInfo(gridKey, in).getCelldata());
        }
        if (!noClioudIndex.isEmpty()) {
            List<LuckySheetCell> cellsDetail = getCellsDetail(gridKey, noClioudIndex);
            finV.putAll(cellsDetail.stream().collect(Collectors.groupingBy(LuckySheetCell::getIndex)));
        }

        return finV;
    }

    @Override
    public List<LuckySheet> getSheetAndAllCells(String gridKey, List<String> index) {
        List<LuckySheet> sheets = getSheet(gridKey, index);
        Map<String, List<LuckySheetCell>> cells = getCells(gridKey, index);
        sheets.forEach(s -> {
            List<LuckySheetCell> sh;
            s.setCelldata((sh = cells.get(s.getIndex())) == null ? new ArrayList<>() : sh);
        });
        return sheets;
    }


    private List<LuckySheet> getSheet(String gridKey, List<String> index) {
        LambdaQueryWrapper<LuckySheet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LuckySheet::getGridKey, gridKey)
                .in(!CollectionUtils.isEmpty(index), LuckySheet::getIndex, index)
                .orderByDesc(LuckySheet::getOrder);
        return Optional.ofNullable(sheetService.list(wrapper)).orElseGet(Collections::emptyList);
    }

    private List<LuckySheetCell> getCellsDetail(String gridKey, List<String> index) {
        LambdaQueryWrapper<LuckySheetCell> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .select(LuckySheetCell::getC, LuckySheetCell::getR, LuckySheetCell::getV, LuckySheetCell::getIndex)
                .eq(LuckySheetCell::getGridKey, gridKey)
                .in(LuckySheetCell::getIndex, index);
        return Optional.ofNullable(sheetCellService.list(wrapper)).orElseGet(Collections::emptyList);
    }

}
