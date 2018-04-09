package ru.alexgryaznov.fltelegram.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.alexgryaznov.fltelegram.dao.ChatRepository;
import ru.alexgryaznov.fltelegram.model.Chat;
import ru.alexgryaznov.fltelegram.model.Project;

@Component
public class TelegramService extends TelegramLongPollingBot {

    private static final String BOT_USERNAME = "FreelanceNotificationsBot";
    private static final String BOT_TOKEN = "552373266:AAFR567O55SZs8pco7mk6i6us_YLJasO7IA";

    private static final String LINE_BREAK = System.lineSeparator();

    private static final String ALREADY_SUBSCRIBED_TEXT = "Already subscribed";
    private static final String SUCCESSFULLY_SUBSCRIBED_TEXT = "Successfully subscribed";

    private final ChatRepository chatRepository;

    static {
        ApiContextInitializer.init();
    }

    @Autowired
    public TelegramService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
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
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @SneakyThrows
    public void sendProjectNotification(Project project) {
        for (Chat chat : chatRepository.findAll()) {
            final String text = project.getTitle() + LINE_BREAK + project.getDescription() + LINE_BREAK + project.getLink();
            execute(new SendMessage(chat.getId(), text));
        }
    }
}
