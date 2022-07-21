package com.luckysheet.luckysheetservice.socket.decoder;

import com.alibaba.fastjson.JSONObject;
import com.luckysheet.luckysheetservice.socket.entity.RequestData;
import com.luckysheet.luckysheetservice.util.GzipUtil;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * @ClassName RequestDecoder
 * @Description 消息解析
 * @Author Quyq
 * @Date 2022/7/12 14:51
 **/
@Slf4j
public class RequestDecoder implements Decoder.Text<RequestData<?>>{
    @Override
    public RequestData<?> decode(String s) throws DecodeException {
        long receiptTime = System.currentTimeMillis();
        try {
            s = GzipUtil.unCompressToURI(s);
        } catch (Exception e) {
            RequestData<String> otherMess = new RequestData<>();
            otherMess.setV(s);
            otherMess.setReceiptTime(receiptTime);
            return otherMess;
        }

        JSONObject sJson;
        try{
            sJson = JSONObject.parseObject(s);
        }catch (Exception e){
            throw new IllegalArgumentException("非法的消息字符串");
        }
        final RequestData<?> data = sJson.toJavaObject(RequestData.class);
        data.setReceiptTime(receiptTime);
        return data;
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
