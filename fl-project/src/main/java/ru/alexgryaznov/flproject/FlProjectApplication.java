package ru.alexgryaznov.flproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlProjectApplication.class, args);
	}
}
