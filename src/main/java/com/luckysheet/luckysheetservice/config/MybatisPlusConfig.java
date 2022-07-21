package com.luckysheet.luckysheetservice.config;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MybatisPlusConfig
 * @Description TODO
 * @Author Quyq
 * @Date 2022/7/11 11:28
 **/
@Configuration
@MapperScan("com.luckysheet.luckysheetservice.mapper")
public class MybatisPlusConfig {

    @Bean
    public PaginationInnerInterceptor mybatisPlusInterceptor() {
        return new PaginationInnerInterceptor();
    }


}
