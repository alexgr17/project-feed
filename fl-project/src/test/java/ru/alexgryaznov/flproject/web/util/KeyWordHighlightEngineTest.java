package ru.alexgryaznov.flproject.web.util;

import ru.alexgryaznov.flproject.service.ProjectService;

public class KeyWordHighlightEngineTest extends AbstractHighlightEngineTest {

    @Override
    protected ProjectService.HighlightEngine getHighlightEngine() {
        return new KeyWordHighlightEngine();
    }

    @Override
    protected String getCssClassName() {
        return "key-word";
    }
}
