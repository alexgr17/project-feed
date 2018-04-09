package ru.alexgryaznov.flproject.service;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import ru.alexgryaznov.flproject.dao.KeyWordRepository;
import ru.alexgryaznov.flproject.dao.ProjectRepository;
import ru.alexgryaznov.flproject.dao.RssFeedRepository;
import ru.alexgryaznov.flproject.domain.KeyWord;
import ru.alexgryaznov.flproject.domain.Project;
import ru.alexgryaznov.flproject.domain.StopWord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProjectServiceTest {

    private ProjectRepository projectRepository;
    private KeyWordRepository keyWordRepository;
    private StopWordService stopWordService;
    private ProjectService projectService;

    @Before
    public void setUp() {
        projectRepository = mock(ProjectRepository.class);
        keyWordRepository = mock(KeyWordRepository.class);
        stopWordService = mock(StopWordService.class);
        projectService = new ProjectService(projectRepository, mock(RssFeedRepository.class), keyWordRepository, stopWordService);
    }

    @Test
    public void testUpdateProjectWasRead() {

        final Date date = new Date();
        final Project project = new Project();
        when(projectRepository.findByWasReadFalseAndPubDateLessThanEqual(date)).thenReturn(Collections.singletonList(project));

        projectService.updateProjectWasRead(date);

        assertTrue(project.isWasRead());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void testGetFLProjectsWithKeyWordInTitle() {

        final Project project = new Project();
        project.setTitle("Test KeyWord Test");
        project.setContent("Test");

        configureMocks(project);

        final Iterable<Project> projects = projectService.getFLProjects(getHighlightEngine(), getHighlightEngine());

        assertProjects(project, projects);
        assertTrue(project.isHasKeyWordInTitle());
        assertFalse(project.isHasStopWordInTitle());
        assertTrue(project.getKeyWordMatchesInContent().isEmpty());
        assertTrue(project.getStopWordMatchesInContent().isEmpty());
    }

    @Test
    public void testGetFLProjectsWithKeyWordInContent() {

        final Project project = new Project();
        project.setTitle("Test");
        project.setContent("Test KeyWord Test");

        configureMocks(project);

        final Iterable<Project> projects = projectService.getFLProjects(getHighlightEngine(), getHighlightEngine());

        assertProjects(project, projects);
        assertFalse(project.isHasKeyWordInTitle());
        assertFalse(project.isHasStopWordInTitle());
        assertFalse(project.getKeyWordMatchesInContent().isEmpty());
        assertTrue(project.getStopWordMatchesInContent().isEmpty());
    }

    @Test
    public void testGetFLProjectsWithStopWordInTitle() {

        final Project project = new Project();
        project.setTitle("Test StopWord Test");
        project.setContent("Test");

        configureMocks(project);

        final Iterable<Project> projects = projectService.getFLProjects(getHighlightEngine(), getHighlightEngine());

        assertProjects(project, projects);
        assertFalse(project.isHasKeyWordInTitle());
        assertTrue(project.isHasStopWordInTitle());
        assertTrue(project.getKeyWordMatchesInContent().isEmpty());
        assertTrue(project.getStopWordMatchesInContent().isEmpty());
    }

    @Test
    public void testGetFLProjectsWithStopWordInContent() {

        final Project project = new Project();
        project.setTitle("Test");
        project.setContent("Test StopWord Test");
        configureMocks(project);

        final Iterable<Project> projects = projectService.getFLProjects(getHighlightEngine(), getHighlightEngine());

        assertProjects(project, projects);
        assertFalse(project.isHasKeyWordInTitle());
        assertFalse(project.isHasStopWordInTitle());
        assertTrue(project.getKeyWordMatchesInContent().isEmpty());
        assertFalse(project.getStopWordMatchesInContent().isEmpty());
    }

    @Test
    public void testGetUpworkProjects() {

        final Project project = new Project();
        when(projectRepository.findByWasReadFalseAndRssFeedInOrderByPubDateDesc(any())).thenReturn(Collections.singletonList(project));

        final Iterable<Project> projects = projectService.getUpworkProjects();

        assertProjects(project, projects);
    }

    private KeyWord getKeyWord() {
        final KeyWord keyWord = new KeyWord();
        keyWord.setTitle("keyword");
        return keyWord;
    }

    private StopWord getStopWord() {
        final StopWord stopWord = new StopWord();
        stopWord.setTitle("stopword");
        return stopWord;
    }

    private ProjectService.HighlightEngine getHighlightEngine() {
        return (string, word) -> string;
    }

    private void assertProjects(Project project, Iterable<Project> projects) {
        final ArrayList<Project> projectList = Lists.newArrayList(projects);
        assertEquals(1, projectList.size());
        assertEquals(project, projectList.get(0));
    }

    private void configureMocks(Project project) {
        when(projectRepository.findByWasReadFalseAndRssFeedInOrderByPubDateDesc(any())).thenReturn(Collections.singletonList(project));
        when(keyWordRepository.findAll()).thenReturn(Collections.singletonList(getKeyWord()));
        when(stopWordService.getStopWords()).thenReturn(Collections.singleton(getStopWord()));
    }
}