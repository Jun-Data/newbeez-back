package com.newbeez.newbeezback;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 브라우저가 직접 부르는 주소만 연다 - 프론트 ARCHITECTURE §6 의 CORS 표와 같아야 한다
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // 허용 출처 - .env의 NEWBEEZ_CORS_ALLOWED_ORIGINS
    private final String[] allowedOrigins;

    public CorsConfig(@Value("${newbeez.cors.allowed-origins}") String[] allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/participants/count")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET");
        registry.addMapping("/api/participants")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("POST")
                .allowedHeaders("Content-Type");
    }
}
