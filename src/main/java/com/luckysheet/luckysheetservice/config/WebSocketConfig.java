package com.luckysheet.luckysheetservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;


/**
 * @ClassName WebSocketConfig
 * @Description TODO
 * @Author Quyq
 * @Date 2022/7/12 15:31
 **/
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebContainer(){
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(5120000);
        container.setMaxBinaryMessageBufferSize(5120000);
        container.setMaxSessionIdleTimeout(15 * 60000L);
        return container;
    }

}
