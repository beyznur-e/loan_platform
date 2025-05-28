package com.example.loan_platform.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Tüm endpoint'lere CORS uygulanır
                        .allowedOrigins("http://127.0.0.1:5500") // Frontend'in çalıştığı port
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // İzin verilen HTTP metotları
                        .allowedHeaders("*") // Her türlü header'a izin ver
                        .allowCredentials(true); // Kimlik doğrulama için izin ver
            }
        };
    }
}

