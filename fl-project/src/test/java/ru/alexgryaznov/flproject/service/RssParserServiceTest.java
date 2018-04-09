package ru.alexgryaznov.flproject.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import ru.alexgryaznov.flproject.domain.Category;
import ru.alexgryaznov.flproject.domain.Project;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RssParserServiceTest {

    private static final String TEST_TITLE = "TEST_TITLE";
    private static final String TEST_LINK = "TEST_LINK";
    private static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    private static final String TEST_GUID = "TEST_GUID";
    private static final String TEST_CATEGORY = "TEST_CATEGORY";
    private static final String TEST_DATE = "Fri, 06 Apr 2018 16:38:57 GMT";

    private static final String TEST_URL = "TEST_URL";

    private RestTemplate restTemplate;
    private RssParserService rssParserService;

    @Before
    public void setUp() {
        restTemplate = mock(RestTemplate.class);
        rssParserService = new RssParserService(restTemplate);
    }

    @Test
    public void testLoadProjects() {

        when(restTemplate.getForObject(TEST_URL, String.class)).thenReturn("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<rss version=\"2.0\">\n" +
                "  <channel>" +
                "    <item>\n" +
                "      <title><![CDATA[" + TEST_TITLE + "]]></title> \n" +
                "      <link>" + TEST_LINK + "</link>\n" +
                "      <description><![CDATA[" + TEST_DESCRIPTION + "]]></description>\n" +
                "      <guid>" + TEST_GUID + "</guid>\n" +
                "      <category><![CDATA[" + TEST_CATEGORY + "]]></category>\n" +
                "      <pubDate>" + TEST_DATE + "</pubDate>\n" +
                "    </item>\n" +
                "  </channel>\n" +
                "</rss>");

        final List<Project> projects = rssParserService.loadProjects(TEST_URL);
        assertEquals(1, projects.size());

        final Project project = projects.get(0);
        assertEquals(TEST_TITLE, project.getTitle());
        assertEquals(TEST_LINK, project.getLink());
        assertEquals(TEST_DESCRIPTION, project.getDescription());
        assertEquals(TEST_GUID, project.getGuid());
        assertNotNull(project.getPubDate());

        final List<Category> categories = project.getCategories();
        assertEquals(1, categories.size());

        final Category category = categories.get(0);
        assertEquals(TEST_CATEGORY, category.getTitle());
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidFormat() {
        when(restTemplate.getForObject(TEST_URL, String.class)).thenReturn("SOME_CRAP");
        rssParserService.loadProjects(TEST_URL);
    }

    @Test
    public void testEmpty() {
        when(restTemplate.getForObject(TEST_URL, String.class)).thenReturn("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<rss version=\"2.0\">\n" +
                "  <channel>" +
                "  </channel>\n" +
                "</rss>");
        assertTrue(rssParserService.loadProjects(TEST_URL).isEmpty());
    }
}