package com.fly.company.f4u_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("https://<TU_FRONTEND>.azurestaticapps.net")
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowCredentials(true);
    }
}
