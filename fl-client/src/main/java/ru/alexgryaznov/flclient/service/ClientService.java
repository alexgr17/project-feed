package ru.alexgryaznov.flclient.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.alexgryaznov.flclient.dao.ClientRepository;
import ru.alexgryaznov.flclient.domain.Client;

@Component
public class ClientService {

    private static final String CLIENT_ONLINE_MARKER = "b-icon b-icon__lamp";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ClientRepository clientRepository;
    private final RestTemplate internalRestTemplate;
    private final RestTemplate externalRestTemplate;

    @Autowired
    public ClientService(ClientRepository clientRepository, RestTemplate internalRestTemplate, RestTemplateBuilder restTemplateBuilder) {
        this.clientRepository = clientRepository;
        this.internalRestTemplate = internalRestTemplate;
        this.externalRestTemplate = restTemplateBuilder.build();
    }

    @Scheduled(fixedRate = 300_000)
    @Transactional
    public void checkClientsOnline() {
        for (Client client : clientRepository.findAll()) {
            final String html = externalRestTemplate.getForObject(client.getUrl(), String.class);
            final boolean online = html.contains(CLIENT_ONLINE_MARKER);
            if (online && !client.isOnline()) {
                log.info("client is online: {}", client.getId());
                internalRestTemplate.postForObject("http://FLTELEGRAM/send-client", new HttpEntity<>(client), String.class);
            }
            client.setOnline(online);
        }
    }
}
