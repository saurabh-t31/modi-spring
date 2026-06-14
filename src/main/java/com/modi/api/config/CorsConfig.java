package com.modi.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String allowedOrigins = System.getenv("CORS_ORIGINS");
        String[] origins;
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            origins = allowedOrigins.split(",");
        } else {
            origins = new String[]{"http://localhost:5173", "http://localhost:3000"};
        }
        registry.addMapping("/api/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*");
    }
}