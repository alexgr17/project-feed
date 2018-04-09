package ru.alexgryaznov.fldiscoveryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class FlDiscoveryServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlDiscoveryServerApplication.class, args);
	}
}
