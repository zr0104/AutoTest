package com.course;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableScheduling
//@SpringBootApplication
@SpringBootApplication
@MapperScan("com.course.mapper") // 替换为你的 Mapper 接口所在包
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
