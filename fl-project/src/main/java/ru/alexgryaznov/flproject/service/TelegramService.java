package ru.alexgryaznov.flproject.service;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.alexgryaznov.flproject.domain.Project;

@Component
public class TelegramService {

    private final RestTemplate restTemplate;

    public TelegramService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendNotification(Project project) {
        final HttpEntity<Project> requestEntity = new HttpEntity<>(project);
        restTemplate.postForObject("http://FLTELEGRAM/send", requestEntity, String.class);
    }
}
