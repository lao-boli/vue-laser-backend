package com.hquyyp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 文件路径与请求路径映射
 *
 * @param
 * @return
 */
@Configuration
public class FileConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 文件磁盘url 映射
        // 配置server虚拟路径，handler为前台访问的目录，locations为files相对应的本地路径
        //此处将  /PlaybackData/  映射为右侧的路径
        //windows下
        //registry.addResourceHandler("/picture/**").addResourceLocations("file:D:/Testpic/");//linux,注意Linux中不要有//
        //若是java:"file:E:\\\\wetemHeadUrlbProject\\FileWebsite\\PlaybackData\\"  (其中\\可以换为/)
        //http://localhost:13389/PlaybackData/434524.txt
        //映射为:/home/javauser/project1/logs/files/flydata/PlaybackData/434524.txt
        //windows下
       registry.addResourceHandler("/picture/**").addResourceLocations("file:/");
    }
}
