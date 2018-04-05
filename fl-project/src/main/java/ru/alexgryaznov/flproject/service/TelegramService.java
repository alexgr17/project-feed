package ru.alexgryaznov.flproject.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.alexgryaznov.flproject.domain.Project;

@Component
public class TelegramService {

    public static final String URL = "http://localhost:8082/send";

    private final RestTemplate restTemplate;

    public TelegramService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public void sendNotification(Project project) {
        final HttpEntity<Project> requestEntity = new HttpEntity<>(project);
        restTemplate.postForObject(URL, requestEntity, String.class);
    }
}
