package ru.alexgryaznov.flproject.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import ru.alexgryaznov.flproject.dao.CategoryRepository;
import ru.alexgryaznov.flproject.dao.ProjectRepository;
import ru.alexgryaznov.flproject.dao.RssFeedRepository;
import ru.alexgryaznov.flproject.domain.Category;
import ru.alexgryaznov.flproject.domain.Project;
import ru.alexgryaznov.flproject.domain.RssFeed;
import ru.alexgryaznov.flproject.domain.RssFeedType;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ScheduledTaskServiceTest {

    private static final String RSS_FEED_URL = "RSS_FEED_URL";

    private static final String PROJECT_GUID = "PROJECT_GUID";
    private static final String PROJECT_LINK = "PROJECT_LINK/projects/0/PROJECT_LINK";
    private static final String PROJECT_CONTENT = "<div>PROJECT_CONTENT</div>";

    private static final String TEST_HTML = "<html><body>SOME_CRAP<div id=\"projectp0\">PROJECT_CONTENT</div>SOME_CRAP</body></html>";

    private RssFeedRepository rssFeedRepository;
    private ProjectRepository projectRepository;
    private CategoryRepository categoryRepository;
    private RssParserService rssParserService;
    private ProjectService projectService;
    private TelegramService telegramService;
    private RestTemplate restTemplate;

    private ScheduledTaskService scheduledTaskService;

    @Before
    public void setUp() {

        rssFeedRepository = mock(RssFeedRepository.class);
        projectRepository = mock(ProjectRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        rssParserService = mock(RssParserService.class);
        projectService = mock(ProjectService.class);
        telegramService = mock(TelegramService.class);
        restTemplate = mock(RestTemplate.class);
        scheduledTaskService = new ScheduledTaskService(rssFeedRepository, projectRepository, categoryRepository,
                rssParserService, projectService, telegramService, restTemplate);
    }

    @Test
    public void loadProjectsWithoutNotification() {

        final RssFeed rssFeed = getRssFeed();
        final Category category = new Category();
        final Project project = getProject(category);

        when(rssFeedRepository.findAll()).thenReturn(Collections.singleton(rssFeed));
        when(rssParserService.loadProjects(RSS_FEED_URL)).thenReturn(Collections.singletonList(project));

        scheduledTaskService.loadProjects();

        assertEquals(rssFeed, project.getRssFeed());
        verify(projectRepository, times(1)).save(project);
        verify(categoryRepository, times(1)).save(category);
        verify(telegramService, never()).sendNotification(any());
    }

    @Test
    public void loadProjectsWithNotification() {

        final RssFeed rssFeed = getRssFeed();
        final Category category = new Category();
        final Project project = getProject(category);

        when(rssFeedRepository.findAll()).thenReturn(Collections.singleton(rssFeed));
        when(rssParserService.loadProjects(RSS_FEED_URL)).thenReturn(Collections.singletonList(project));

        doAnswer(invocationOnMock -> {
            for (Project p : invocationOnMock.<Iterable<Project>>getArgument(2)) {
                p.setHasKeyWordInTitle(true);
            }
            return null;
        }).when(projectService).processWordsInProjectTitle(any(), any(), any());

        scheduledTaskService.loadProjects();

        assertEquals(rssFeed, project.getRssFeed());
        verify(projectRepository, times(1)).save(project);
        verify(categoryRepository, times(1)).save(category);
        verify(telegramService, times(1)).sendNotification(project);
    }

    @Test
    public void loadProjectsAlreadyExists() {

        final RssFeed rssFeed = getRssFeed();
        final Category category = new Category();
        final Project project = getProject(category);

        when(rssFeedRepository.findAll()).thenReturn(Collections.singleton(rssFeed));
        when(rssParserService.loadProjects(RSS_FEED_URL)).thenReturn(Collections.singletonList(project));
        when(projectRepository.findById(PROJECT_GUID)).thenReturn(Optional.of(project));

        scheduledTaskService.loadProjects();

        verify(projectRepository, never()).save(any());
        verify(categoryRepository, never()).save(any());
        verify(telegramService, never()).sendNotification(any());
    }

    @Test
    public void loadContentForFLProjectsNoNewProjects() throws InterruptedException {

        when(projectRepository.findByContentIsNullAndRssFeedIn(any())).thenReturn(Collections.emptyList());

        scheduledTaskService.loadContentForFLProjects();

        verify(restTemplate, never()).getForObject(any(), any());
        verify(projectRepository, never()).save(any());
        verify(telegramService, never()).sendNotification(any());
    }

    @Test
    public void loadContentForFLProjectsWithoutNotification() throws InterruptedException {

        final Category category = new Category();
        final Project project = getProject(category);

        when(projectRepository.findByContentIsNullAndRssFeedIn(any())).thenReturn(Collections.singletonList(project));
        when(restTemplate.getForObject(PROJECT_LINK, String.class)).thenReturn(TEST_HTML);

        scheduledTaskService.loadContentForFLProjects();

        assertEquals(PROJECT_CONTENT, project.getContent());
        verify(projectRepository, times(1)).save(project);
        verify(telegramService, never()).sendNotification(any());
    }

    @Test
    public void loadContentForFLProjectsWithNotification() throws InterruptedException {

        final Category category = new Category();
        final Project project = getProject(category);

        when(projectRepository.findByContentIsNullAndRssFeedIn(any())).thenReturn(Collections.singletonList(project));
        when(restTemplate.getForObject(PROJECT_LINK, String.class)).thenReturn(TEST_HTML);

        doAnswer(invocationOnMock -> {
            for (Project p : invocationOnMock.<Iterable<Project>>getArgument(2)) {
                p.setKeyWordMatchesInContent(Collections.singletonList("test"));
            }
            return null;
        }).when(projectService).processWordsInProjectContent(any(), any(), any());

        scheduledTaskService.loadContentForFLProjects();

        assertEquals(PROJECT_CONTENT, project.getContent());
        verify(projectRepository, times(1)).save(project);
        verify(telegramService, times(1)).sendNotification(any());
    }

    private RssFeed getRssFeed() {
        final RssFeed rssFeed = new RssFeed();
        rssFeed.setUrl(RSS_FEED_URL);
        rssFeed.setType(RssFeedType.FL.name());
        return rssFeed;
    }

    private Project getProject(Category category) {
        final Project project = new Project();
        project.setGuid(PROJECT_GUID);
        project.setLink(PROJECT_LINK);
        project.setCategories(Collections.singletonList(category));
        project.setKeyWordMatchesInContent(Collections.emptyList());
        return project;
    }
}