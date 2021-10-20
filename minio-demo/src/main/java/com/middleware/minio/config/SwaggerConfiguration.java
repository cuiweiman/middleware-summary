package com.middleware.minio.config;

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
 * @description:
 * @author: cuiweiman
 * @date: 2021/8/26 19:20
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Value("${swagger.switch}")
    private boolean enableSwagger;

    @Bean
    public Docket minioAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(enableSwagger)
                //分组名称
                .groupName("minio APIs")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.middleware.minio.modules.minio"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket systemAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(enableSwagger)
                //分组名称
                .groupName("System APIs")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.middleware.minio.modules.system"))
                .paths(PathSelectors.any())
                .build();
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("MinIO Demo RESTful APIs")
                .description("# MinIO Demo RESTful APIs")
                .termsOfServiceUrl("http://127.0.0.1:8300/minio")
                .contact(new Contact("author", "http://127.0.0.1:8300/minio", "email@163.com"))
                .version("V1.0")
                .build();
    }

}
