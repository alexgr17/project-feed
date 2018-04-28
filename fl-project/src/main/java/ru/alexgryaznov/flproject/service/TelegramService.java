package ru.alexgryaznov.flproject.service;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.alexgryaznov.flproject.domain.Project;

@Component
public class TelegramService {

    private final RestTemplate internalRestTemplate;

    public TelegramService(RestTemplate internalRestTemplate) {
        this.internalRestTemplate = internalRestTemplate;
    }

    public void sendNotification(Project project) {
        internalRestTemplate.postForObject("http://FLTELEGRAM/send-project", new HttpEntity<>(project), String.class);
    }
}
