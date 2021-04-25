package com.guangmushikong.lbi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/*************************************
 * Class Name: Application
 * Description:〈启动入口〉
 * @author deyi
 * @since 1.0.0
 ************************************/
@EnableCaching
@SpringBootApplication
public class Application {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
