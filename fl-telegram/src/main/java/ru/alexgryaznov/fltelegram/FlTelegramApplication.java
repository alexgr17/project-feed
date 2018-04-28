package ru.alexgryaznov.fltelegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ru.alexgryaznov.fltelegram.service.TelegramService;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

import static springfox.documentation.builders.PathSelectors.any;

@SpringBootApplication
@EnableSwagger2
@EnableDiscoveryClient
@EnableConfigurationProperties(TelegramProperties.class)
public class FlTelegramApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlTelegramApplication.class, args);
	}

	@Bean
	public TelegramBotsApi telegramBotsApi(TelegramService telegramService) throws TelegramApiRequestException {
		final TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		telegramBotsApi.registerBot(telegramService);
		return telegramBotsApi;
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("ru.alexgryaznov.fltelegram")
				.select()
				.apis(RequestHandlerSelectors.basePackage("ru.alexgryaznov.fltelegram"))
				.paths(any())
				.build()
				.apiInfo(new ApiInfo("FL Telegram", "Telegram notifications for new FL projects", "0.0.1-SNAPSHOT",
						null, null, null, null, Collections.emptyList()));
	}
}
