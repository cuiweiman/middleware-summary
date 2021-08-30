package com.middleware.minio.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: MinIO config
 * @author: cuiweiman
 * @date: 2021/8/26 14:45
 */
@Data
@Configuration
@ConfigurationProperties(value = "minio")
public class MinioConfiguration {

    private String endpoint;

    private Integer port;

    /**
     * 是否使用 https
     */
    private Boolean secure;

    private String accessKey;

    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint, port, secure)
                .credentials(accessKey, secretKey)
                .build();
    }

}
