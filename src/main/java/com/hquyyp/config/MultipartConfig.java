package com.hquyyp.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import javax.servlet.MultipartConfigElement;

/**
 * 设置multipartfile的上传文件大小
 */
@Configuration
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //单个文件最大
        //factory.setMaxFileSize(DataSize.ofMegabytes(5)); //MB
        //factory.setMaxFileSize(DataSize.ofKilobytes(80)); //KB
        factory.setMaxFileSize(DataSize.ofGigabytes(2)); //Gb
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.ofGigabytes(2));
        return factory.createMultipartConfig();
    }
}
