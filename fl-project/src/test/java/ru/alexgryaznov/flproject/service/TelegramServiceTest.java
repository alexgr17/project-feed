package ru.alexgryaznov.flproject.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import ru.alexgryaznov.flproject.domain.Project;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TelegramServiceTest {

    private TelegramService telegramService;
    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        restTemplate = mock(RestTemplate.class);
        telegramService = new TelegramService(restTemplate);
    }

    @Test
    public void testSendNotification() {
        telegramService.sendNotification(new Project());
        verify(restTemplate, times(1)).postForObject(eq("http://localhost:8082/send"), any(), any());
    }
}