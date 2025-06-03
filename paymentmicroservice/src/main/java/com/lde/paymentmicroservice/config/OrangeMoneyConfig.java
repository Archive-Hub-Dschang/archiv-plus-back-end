package com.lde.paymentmicroservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "orange.api")
@Data
public class OrangeMoneyConfig {
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    private String callbackUrl;
}
