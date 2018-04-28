package ru.alexgryaznov.fltelegram.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.alexgryaznov.fltelegram.model.Client;
import ru.alexgryaznov.fltelegram.model.Project;
import ru.alexgryaznov.fltelegram.service.TelegramService;

@RestController
public class TelegramController {

    private final TelegramService telegramService;

    @Autowired
    public TelegramController(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @RequestMapping(value = "/send-project", method = RequestMethod.POST)
    public void sendProject(@RequestBody Project project) {
        telegramService.sendProjectNotification(project);
    }

    @RequestMapping(value = "/send-client", method = RequestMethod.POST)
    public void sendClient(@RequestBody Client client) {
        telegramService.sendClientNotification(client);
    }
}
