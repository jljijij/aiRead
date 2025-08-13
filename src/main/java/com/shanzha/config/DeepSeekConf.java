package com.shanzha.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
@Component
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekConf {
    private String apiKey;
    private String apiHost;
    private Long timeout;

    public DeepSeekConf() {
        System.out.println(">>> DeepSeekConf constructor called");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println(">>> DeepSeekConf PostConstruct");
        System.out.printf(">>> apiKey: %s, timeout: %s%n", apiKey, timeout);
    }
}
