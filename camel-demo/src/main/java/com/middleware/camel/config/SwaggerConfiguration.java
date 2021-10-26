package com.middleware.camel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @description: swagger config
 * @author: cuiweiman
 * @date: 2021/10/20 20:52
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Value("${swagger.switch}")
    private boolean enableSwagger;

    @Bean
    public Docket mysql2KafkaAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(enableSwagger)
                //分组名称
                .groupName("MySQL to Kafka APIs")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.middleware.camel.module.mysqltokafka.controller"))
                .paths(PathSelectors.any())
                .build();
    }


    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Camel Demo RESTful APIs")
                .description("# Camel Demo RESTful APIs")
                .termsOfServiceUrl("http://127.0.0.1:8302/camel")
                .contact(new Contact("author", "http://127.0.0.1:8302/camel", "email@163.com"))
                .version("V1.0")
                .build();
    }

}
