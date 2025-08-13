package com.shanzha.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioConfigProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
}

