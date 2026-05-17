package com.tcg.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TcgPortalApplication {
    public static void main(String[] args) {
        SpringApplication.run(TcgPortalApplication.class, args);
    }
}
