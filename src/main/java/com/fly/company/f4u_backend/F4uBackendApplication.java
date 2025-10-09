package com.fly.company.f4u_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class F4uBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(F4uBackendApplication.class, args);
    }
}
