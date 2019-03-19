package com.guangmushikong.lbi.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger2的Java配置类
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Value("${spring.cloud.client.ip-address}")
    String application_node_ip;
    @Value("${server.port}")
    int server_port;

    /**
     * Swagger2的配置文件：内容、扫描包等
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                //.forCodeGeneration(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.guangmushikong.lbi"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 构建api文档的详细信息函数
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("瓦片地图服务 RESTful APIs")
                .description("服务器地址：http://"+application_node_ip+":"+server_port)
                .termsOfServiceUrl("http://"+application_node_ip+":"+server_port+"/")
                .version("0.0.1")
                .build();
    }
}
