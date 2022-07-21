package com.luckysheet.luckysheetservice.socket.excep;

/**
 * @ClassName LuckySheetParameterException
 * @Description 同步数据异常
 * @Author Quyq
 * @Date 2022/7/14 15:28
 **/
public class LuckySheetSyncDataException extends RuntimeException{

    private final String gridKey;

    private final String index;

    private final String[] param;

    private static final String mess = "数据同步异常：%s 工作簿 -> %s sheet -> %s 属性";

    public LuckySheetSyncDataException(String gridKey,String index,Throwable cause,String... param){
        super(String.format(mess,gridKey,index,String.join(",",param)),cause);
        this.gridKey = gridKey;
        this.index = index;
        this.param = param;

    }
    public LuckySheetSyncDataException(String gridKey,String index,String... param){
        super(String.format(mess,gridKey,index,String.join(",",param)));
        this.gridKey = gridKey;
        this.index = index;
        this.param = param;

    }

    public String getGridKey() {
        return gridKey;
    }

    public String getIndex() {
        return index;
    }

    public String[] getParam() {
        return param;
    }
}
