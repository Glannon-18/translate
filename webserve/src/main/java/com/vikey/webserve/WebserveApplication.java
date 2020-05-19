package com.vikey.webserve;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.vikey.webserve.mapper.xml")
public class WebserveApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebserveApplication.class, args);
    }

}
