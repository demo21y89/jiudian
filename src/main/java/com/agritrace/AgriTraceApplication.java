package com.agritrace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgriTraceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgriTraceApplication.class, args);
    }
}
