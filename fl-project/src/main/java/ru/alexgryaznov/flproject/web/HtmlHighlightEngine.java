package ru.alexgryaznov.flproject.web;

import org.springframework.stereotype.Component;
import ru.alexgryaznov.flproject.domain.StopWord;
import ru.alexgryaznov.flproject.service.ProjectService;

@Component
public class HtmlHighlightEngine implements ProjectService.HighlightEngine {

    private static final String STOP_WORD_TAG_OPEN = "<span class=\"stop-word\">";
    private static final String STOP_WORD_TAG_CLOSE = "</span>";

    @Override
    public String highlightStopWord(String string, StopWord stopWord) {
        return stopWord.getPattern()
                .matcher(string)
                .replaceAll(STOP_WORD_TAG_OPEN + stopWord.getTitle() + STOP_WORD_TAG_CLOSE);
    }
}
