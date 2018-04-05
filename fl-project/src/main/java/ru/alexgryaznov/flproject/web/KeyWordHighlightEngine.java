package ru.alexgryaznov.flproject.web;

import org.springframework.stereotype.Component;

@Component
public class KeyWordHighlightEngine extends AbstractHighlightEngine {

    @Override
    protected String getCssClassName() {
        return "key-word";
    }
}
