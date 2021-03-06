package ru.alexgryaznov.fltelegram.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.alexgryaznov.fltelegram.TelegramProperties;
import ru.alexgryaznov.fltelegram.dao.ChatRepository;
import ru.alexgryaznov.fltelegram.model.Chat;
import ru.alexgryaznov.fltelegram.model.Client;
import ru.alexgryaznov.fltelegram.model.Project;

@Component
public class TelegramService extends TelegramLongPollingBot {

    private static final String LINE_BREAK = System.lineSeparator();

    private static final String ALREADY_SUBSCRIBED_TEXT = "Already subscribed";
    private static final String SUCCESSFULLY_SUBSCRIBED_TEXT = "Successfully subscribed";
    private static final String CLIENT_ONLINE_TEXT = "Client online: ";

    private final ChatRepository chatRepository;
    private final TelegramProperties telegramProperties;

    static {
        ApiContextInitializer.init();
    }

    @Autowired
    public TelegramService(ChatRepository chatRepository, TelegramProperties telegramProperties) {
        this.chatRepository = chatRepository;
        this.telegramProperties = telegramProperties;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        final Message message = update.getMessage();
        final Long chatId = message.getChatId();

        if (chatRepository.findById(chatId).isPresent()) {
            execute(new SendMessage(chatId, ALREADY_SUBSCRIBED_TEXT));
        } else {
            final Chat chat = new Chat();
            chat.setId(chatId);
            chat.setName(message.getFrom().getUserName());
            chatRepository.save(chat);

            execute(new SendMessage(chatId, SUCCESSFULLY_SUBSCRIBED_TEXT));
        }
    }

    @Override
    public String getBotUsername() {
        return telegramProperties.getBotName();
    }

    @Override
    public String getBotToken() {
        return telegramProperties.getBotToken();
    }

    public void sendProjectNotification(Project project) {
        sendNotification(project.getTitle() + LINE_BREAK + project.getDescription() + LINE_BREAK + project.getLink());
    }

    public void sendClientNotification(Client client) {
        sendNotification(CLIENT_ONLINE_TEXT + client.getUrl());
    }

    @SneakyThrows
    private void sendNotification(String text) {
        for (Chat chat : chatRepository.findAll()) {
            execute(new SendMessage(chat.getId(), text));
        }
    }
}
