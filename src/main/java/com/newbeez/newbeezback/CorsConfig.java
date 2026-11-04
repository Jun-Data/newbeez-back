package com.newbeez.newbeezback;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 브라우저가 직접 부르는 주소만 연다 - 프론트 ARCHITECTURE §6 의 CORS 표와 같아야 한다
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/participants/count")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET");
        registry.addMapping("/api/participants")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("POST")
                .allowedHeaders("Content-Type");
    }
}
