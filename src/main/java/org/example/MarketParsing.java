package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MarketParsing {
    public static void main(String[] args) {
        SpringApplication.run(MarketParsing.class, args);
    }
}
