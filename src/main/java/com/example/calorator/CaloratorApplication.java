package com.example.calorator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CaloratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaloratorApplication.class, args);
    }

}
