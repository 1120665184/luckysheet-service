package com.luckysheet.luckysheetservice.socket.cache;

import com.luckysheet.luckysheetservice.entity.LuckySheet;
import com.luckysheet.luckysheetservice.entity.LuckyWorkbook;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ClassName CacheManager
 * @Description 缓存管理
 * @Author Quyq
 * @Date 2022/7/13 15:17
 **/
public class CacheManager {

    /**
     * 缓存数据，
     * key  gridkey
     * value 缓存信息
     */
    private static final Map<String,WorkbookCache> caches = new ConcurrentHashMap<>();

    /**
     * 判定是否有指定的工作簿信息
     * @param gridKey
     * @return
     */
    public static boolean hasWorkbookInfo(String gridKey){
        return Objects.nonNull(caches.get(gridKey));
    }

    /**
     * 判定工作簿中是否有指定的sheet页数据信息
     * @param gridKey
     * @param index
     * @return
     */
    public static boolean hasSheet(String gridKey ,String index){
        if(caches.get(gridKey) == null)
            return false;

        return Objects.nonNull(caches.get(gridKey).getSheet(index));
    }

    public static void put(LuckyWorkbook workbook){
        caches.put(workbook.getGridKey(), new WorkbookCache(workbook));
    }

    public static boolean put(String gridKey, LuckySheet sheet){
        boolean flag = false;
        Optional<WorkbookCache> baseInfo = Optional.ofNullable(caches.get(gridKey));
        if(baseInfo.isPresent()){
            baseInfo.get().putSheet(sheet);
            flag = true;
        }
        return flag;
    }

    /**
     * 获取工作簿
     * @param gridKey
     * @return
     */
    public static LuckyWorkbook getWorkbook(String gridKey){
        return Optional.ofNullable(caches.get(gridKey))
                .map(WorkbookCache::getBaseInfo).orElse(null);
    }

    /**
     * 获取sheet信息
     * @param gridKey
     * @param index
     * @return
     */
    public static LuckySheet getSheetInfo(String gridKey , String index){
        return Optional.ofNullable(caches.get(gridKey))
                .map(c ->c.getSheet(index)).orElse(null);
    }

    public static void deleteSheet(String gridKey , String index){
        Optional.ofNullable(caches.get(gridKey))
                .ifPresent(v -> {
                    v.deleteSheet(index);
                });
    }

    public static boolean isDeleteSheet(String gridKey , String index){
        AtomicBoolean flag = new AtomicBoolean(false);
        Optional.ofNullable(caches.get(gridKey))
                .ifPresent(v -> {
                    flag.set(v.isDelete(index));
                });
        return flag.get();
    }

    /**
     * 切换到指定sheet为激活状态
     * @param gridKey
     * @param index
     * @return
     */
    public static boolean changeStatusTo(String gridKey , String index){

        return Optional.ofNullable(caches.get(gridKey))
                .map(workbookCache -> workbookCache.changeStatus(index))
                .orElse(false);

    }

    /**
     * 删除并获取最终数据，用于保存
     * @param gridKey
     * @return
     */
    public static ChangedData remove(String gridKey){
        WorkbookCache removed = caches.remove(gridKey);
        if(Objects.isNull(removed))
            return null;
        return new ChangedData(removed.getFinData())
                .setDeleteSheet(removed.getDeleteSheet());
    }


}
