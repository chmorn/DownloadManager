package com.chmorn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author chenxu
 * @version 1.0
 * @className DownloadQqWebApplication
 * @description 启动类
 * @date 2021/7/21
 **/
@SpringBootApplication
@EnableSwagger2
public class DownloadManagerApplication {
    private static Logger logger = LoggerFactory.getLogger(DownloadManagerApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(DownloadManagerApplication.class,args);
    }
}
