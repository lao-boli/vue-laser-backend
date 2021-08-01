package com.hquyyp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.mybatis.spring.annotation.MapperScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@Configuration
@EnableSwagger2
@MapperScan(basePackages = {"com.hquyyp.dao"})
public class SerialPortApplication {
    public static void main(String[] args) {
        SpringApplication.run(SerialPortApplication.class,args);
    }

}
