package ru.alexgryaznov.fltelegram;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telegram")
@Data
public class TelegramProperties {

    private String botName;

    private String botToken;
}
