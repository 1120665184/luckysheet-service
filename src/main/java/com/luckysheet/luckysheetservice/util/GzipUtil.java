package com.luckysheet.luckysheetservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @ClassName GzipUtil
 * @Description TODO
 * @Author Quyq
 * @Date 2022/7/11 16:16
 **/
@Slf4j
public class GzipUtil {


    public static String compress(String str){
        try(
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                GZIPOutputStream gzip = new GZIPOutputStream(out)
        ) {

            gzip.write(str.getBytes(StandardCharsets.UTF_8));
            return out.toString();

        }catch (Exception e){
            log.error("GzipUtils compress",e);
        }
        return null;
    }

    public static String uncompress(String str) throws Exception{
        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes(StandardCharsets.ISO_8859_1));
            GZIPInputStream gunzip = new GZIPInputStream(in)){
            if(!StringUtils.hasText(str)) return str;

            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >=0)
                out.write(buffer , 0 ,n);
            return out.toString();

        }
    }

    public static String unCompressToURI(String jsUriStr) throws Exception{
        if(!StringUtils.hasText(jsUriStr))
            return "";

        return URLDecoder.decode(uncompress(jsUriStr),"UTF-8");
    }

}
