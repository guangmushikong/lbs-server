package com.guangmushikong.lbi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/*************************************
 * Class Name: Application
 * Description:〈启动入口〉
 * @author deyi
 * @since 1.0.0
 ************************************/
@EnableCaching
@SpringBootApplication
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
