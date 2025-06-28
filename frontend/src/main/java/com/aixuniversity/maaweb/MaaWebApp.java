package com.aixuniversity.maaweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.aixuniversity.maaweb"})
public class MaaWebApp {
    public static void main(String[] args) {
        SpringApplication.run(MaaWebApp.class, args);
    }
}
