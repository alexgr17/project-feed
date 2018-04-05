package ru.alexgryaznov.flproject.web;

import org.junit.Before;
import org.junit.Test;
import ru.alexgryaznov.flproject.domain.StopWord;
import ru.alexgryaznov.flproject.service.ProjectService;

import static org.junit.Assert.assertEquals;

public abstract class AbstractHighlightEngineTest {

    private ProjectService.HighlightEngine highlightEngine;
    private StopWord stopWord;

    @Before
    public void setUp() {
        highlightEngine = getHighlightEngine();
        stopWord = new StopWord();
        stopWord.setTitle("title");
    }

    protected abstract ProjectService.HighlightEngine getHighlightEngine();

    protected abstract String getCssClassName();

    @Test
    public void testLoverCase() {
        assertEquals(
                "test <span class=\"" + getCssClassName() + "\">title</span> test",
                highlightEngine.highlightWord("test title test", stopWord)
        );
    }

    @Test
    public void testUpperCase() {
        assertEquals(
                "test <span class=\"" + getCssClassName() + "\">title</span> test",
                highlightEngine.highlightWord("test TITLE test", stopWord)
        );
    }

    @Test
    public void testCombinedCase() {
        assertEquals(
                "test <span class=\"" + getCssClassName() + "\">title</span> test",
                highlightEngine.highlightWord("test TiTlE test", stopWord)
        );
    }

    @Test
    public void testNotFound() {
        assertEquals(
                "test test",
                highlightEngine.highlightWord("test test", stopWord)
        );
    }
}
