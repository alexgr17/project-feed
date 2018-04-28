package ru.alexgryaznov.fltelegram;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telegram")
public class TelegramProperties {

    @Getter
    @Setter
    private String botName;

    @Getter
    @Setter
    private String botToken;
}
