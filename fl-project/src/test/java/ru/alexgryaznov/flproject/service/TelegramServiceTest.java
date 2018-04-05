package ru.alexgryaznov.flproject.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
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

        final RestTemplateBuilder restTemplateBuilder = mock(RestTemplateBuilder.class);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        telegramService = new TelegramService(restTemplateBuilder);
    }

    @Test
    public void testSendNotification() {
        telegramService.sendNotification(new Project());
        verify(restTemplate, times(1)).postForObject(eq(TelegramService.URL), any(), any());
    }
}