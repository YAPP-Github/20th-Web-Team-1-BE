package com.yapp.betree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BeTreeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeTreeApplication.class, args);
    }

}
