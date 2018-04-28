package ru.alexgryaznov.flproject.web.util;

import ru.alexgryaznov.flproject.domain.Word;
import ru.alexgryaznov.flproject.service.ProjectService;

public abstract class AbstractHighlightEngine implements ProjectService.HighlightEngine {

    private static final String TAG_OPEN_FORMAT = "<span class=\"%s\">";
    private static final String TAG_CLOSE = "</span>";

    @Override
    public String highlightWord(String string, Word word) {
        return word.getPattern()
                .matcher(string)
                .replaceAll(String.format(TAG_OPEN_FORMAT, getCssClassName()) + word.getTitle() + TAG_CLOSE);
    }

    protected abstract String getCssClassName();
}
