package ru.alexgryaznov.flproject.web.util;

import org.springframework.stereotype.Component;

@Component
public class StopWordHighlightEngine extends AbstractHighlightEngine {

    @Override
    protected String getCssClassName() {
        return "stop-word";
    }
}
