package com.lde.usermicroservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class ApplicationConfig {
    @Bean
    BCryptPasswordEncoder getpasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
