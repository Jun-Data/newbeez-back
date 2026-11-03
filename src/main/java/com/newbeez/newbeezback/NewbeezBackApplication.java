package com.newbeez.newbeezback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NewbeezBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewbeezBackApplication.class, args);
    }

}
