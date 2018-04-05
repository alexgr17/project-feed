package ru.alexgryaznov.flproject.web;

import org.springframework.stereotype.Component;

@Component
public class StopWordHighlightEngine extends AbstractHighlightEngine {

    @Override
    protected String getCssClassName() {
        return "stop-word";
    }
}
