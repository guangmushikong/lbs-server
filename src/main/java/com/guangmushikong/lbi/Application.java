/**************************************
 * Package: com.guangmushikong.lbi
 * Author: deyi
 * Date: Created in 2019/3/18 16:26
 **************************************/
package com.guangmushikong.lbi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/*************************************
 * Class Name: Application
 * Description:〈启动入口〉
 * @author deyi
 * @create 2019/3/18
 * @since 1.0.0
 ************************************/
@EnableCaching
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@ServletComponentScan
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
