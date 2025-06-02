package com.lde.academicservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class AcademicserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AcademicserviceApplication.class, args);
    }

}
