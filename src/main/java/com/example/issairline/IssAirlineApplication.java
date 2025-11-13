package com.example.issairline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IssAirlineApplication {

    public static void main(String[] args) {
        SpringApplication.run(IssAirlineApplication.class, args);
    }
}
