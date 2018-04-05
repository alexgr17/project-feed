package ru.alexgryaznov.flproject.domain;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectTest {

    @Test
    public void testHasNoKeyWord() {
        final Project project = getProject();
        assertFalse(project.isHasKeyWord());
    }

    @Test
    public void testHasKeyWordInTitle() {
        final Project project = getProject();
        project.setHasKeyWordInTitle(true);
        assertTrue(project.isHasKeyWord());
    }

    @Test
    public void testHasKeyWordInContent() {
        final Project project = getProject();
        project.getKeyWordMatchesInContent().add("test");
        assertTrue(project.isHasKeyWord());
    }

    @Test
    public void testHasKeyWordInTitleAndContent() {
        final Project project = getProject();
        project.setHasKeyWordInTitle(true);
        project.getKeyWordMatchesInContent().add("test");
        assertTrue(project.isHasKeyWord());
    }

    @Test
    public void testNotFiltered() {
        final Project project = getProject();
        assertFalse(project.isFiltered());
    }

    @Test
    public void testFiltered() {
        final Project project = getProject();
        project.setHasStopWordInTitle(true);
        assertTrue(project.isFiltered());
        assertFalse(project.isHasKeyWord());
    }

    @Test
    public void testNotFilteredKeyWordInTitle() {
        final Project project = getProject();
        project.setHasStopWordInTitle(true);
        project.setHasKeyWordInTitle(true);
        assertFalse(project.isFiltered());
        assertTrue(project.isHasKeyWord());
    }

    @Test
    public void testNotFilteredKeyWordInContent() {
        final Project project = getProject();
        project.setHasStopWordInTitle(true);
        project.getKeyWordMatchesInContent().add("test");
        assertFalse(project.isFiltered());
        assertTrue(project.isHasKeyWord());
    }

    @Test
    public void testNotFilteredKeyWordInTitleAndContent() {
        final Project project = getProject();
        project.setHasStopWordInTitle(true);
        project.setHasKeyWordInTitle(true);
        project.getKeyWordMatchesInContent().add("test");
        assertFalse(project.isFiltered());
        assertTrue(project.isHasKeyWord());
    }

    private Project getProject() {
        final Project project = new Project();
        project.setKeyWordMatchesInContent(new ArrayList<>());
        return project;
    }
}