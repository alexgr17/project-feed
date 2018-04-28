package ru.alexgryaznov.flproject.web.util;

import ru.alexgryaznov.flproject.service.ProjectService;

public class StopWordHighlightEngineTest extends AbstractHighlightEngineTest {

    @Override
    protected ProjectService.HighlightEngine getHighlightEngine() {
        return new StopWordHighlightEngine();
    }

    @Override
    protected String getCssClassName() {
        return "stop-word";
    }
}