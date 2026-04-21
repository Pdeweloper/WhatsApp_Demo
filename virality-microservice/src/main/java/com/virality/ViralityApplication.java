package com.virality;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ViralityApplication {
    public static void main(String[] args) {
        SpringApplication.run(ViralityApplication.class, args);
    }
}
