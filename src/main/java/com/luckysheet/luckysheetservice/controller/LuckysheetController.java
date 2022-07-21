package com.luckysheet.luckysheetservice.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luckysheet.luckysheetservice.entity.LuckyWorkbook;
import com.luckysheet.luckysheetservice.form.WorkbookForm;
import com.luckysheet.luckysheetservice.service.ILuckyWorkbookService;
import com.luckysheet.luckysheetservice.socket.cache.CacheManager;
import com.luckysheet.luckysheetservice.socket.session.LuckysheetSession;
import com.luckysheet.luckysheetservice.socket.session.SessionManager;
import com.luckysheet.luckysheetservice.util.AsyncLock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.websocket.CloseReason;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName LuckysheetController
 * @Author Quyq
 * @Date 2022/7/11 16:08
 **/
@RestController
@RequestMapping("/luckysheet")
@Slf4j
public class LuckysheetController {

    @Resource private ILuckyWorkbookService  workbookService;

    @GetMapping
    public ResponseEntity<IPage<LuckyWorkbook>> getByCondition(WorkbookForm form){
        return new ResponseEntity<>(workbookService.findByCondition(form), HttpStatus.OK);
    }

    @GetMapping("/{gridKey}")
    public ResponseEntity<LuckyWorkbook> getDetail(@PathVariable String gridKey){
        return new ResponseEntity<>(workbookService.getById(gridKey),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> insertOrUpdateWorkboot(@RequestBody LuckyWorkbook workbook){
        return new ResponseEntity<>(workbookService.insertOrUpdate(workbook),HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestBody List<String> gridKeys){
        workbookService.delete(gridKeys);
        for (String gridKey : gridKeys){
            CacheManager.remove(gridKey);
            AsyncLock.lock(gridKey);
            SessionManager.getSession(gridKey,s ->{
                try {
                    s.getSession().close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT,"工作簿被删除"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/load")
    public String load(@RequestParam(required = false) String gridKey){

        if(!StringUtils.hasText(gridKey)) return null;


        return JSON.toJSONString(workbookService.getSheetAndStatusCell(gridKey));

    }

    @PostMapping("/loadSheet")
    public String loadSheet(@RequestParam(required = false) String gridKey,
                          @RequestParam(required = false) List<String> index){
        if(!StringUtils.hasText(gridKey) || CollectionUtils.isEmpty(index)) return null;

        return JSON.toJSONString(workbookService.getCells(gridKey, index));


    }


}
