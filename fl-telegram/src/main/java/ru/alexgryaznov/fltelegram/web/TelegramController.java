package ru.alexgryaznov.fltelegram.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.alexgryaznov.fltelegram.model.Project;
import ru.alexgryaznov.fltelegram.service.TelegramService;

@RestController
public class TelegramController {

    private final TelegramService telegramService;

    @Autowired
    public TelegramController(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public void send(@RequestBody Project project) {
        telegramService.sendProjectNotification(project);
    }
}
