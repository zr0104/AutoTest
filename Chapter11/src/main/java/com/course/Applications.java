package com.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Applications {
    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        Applications.context = SpringApplication.run(Applications.class, args);
    }

//    @PreDestroy
//    public void close() {
//        Applications.context.close();
//    }
}
