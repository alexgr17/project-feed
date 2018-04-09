package ru.alexgryaznov.flconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class FlConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlConfigServerApplication.class, args);
    }
}
