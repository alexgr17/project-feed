package ru.alexgryaznov.flclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

import static springfox.documentation.builders.PathSelectors.any;

@SpringBootApplication
@EnableScheduling
@EnableSwagger2
@EnableDiscoveryClient
public class FlClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlClientApplication.class, args);
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("ru.alexgryaznov.flclient")
				.select()
				.apis(RequestHandlerSelectors.basePackage("ru.alexgryaznov.flclient"))
				.paths(any())
				.build()
				.apiInfo(new ApiInfo("FL Client", null, "0.0.1-SNAPSHOT",
						null, null, null, null, Collections.emptyList()));
	}

	@LoadBalanced
	@Bean
	public RestTemplate internalRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder.build();
	}
}
