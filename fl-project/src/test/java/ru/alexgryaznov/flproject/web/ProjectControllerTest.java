package ru.alexgryaznov.flproject.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.alexgryaznov.flproject.dao.KeyWordRepository;
import ru.alexgryaznov.flproject.dao.RssFeedRepository;
import ru.alexgryaznov.flproject.domain.*;
import ru.alexgryaznov.flproject.service.ProjectService;
import ru.alexgryaznov.flproject.service.StopWordService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    private static final String TEST_TITLE = "TEST_TITLE";
    private static final String TEST_LINK = "TEST_LINK";
    private static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    private static final String TEST_GUID = "TEST_GUID";
    private static final String TEST_CATEGORY = "TEST_CATEGORY";

    @MockBean
    private KeyWordRepository keyWordRepository;

    @MockBean
    private RssFeedRepository rssFeedRepository;

    @MockBean
    private StopWordService stopWordService;

    @MockBean
    private ProjectService projectService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testIndex() throws Exception {

        final StopWord stopWord = new StopWord();
        stopWord.setTitle("STOP_WORD");
        when(stopWordService.getStopWords()).thenReturn(Collections.singleton(stopWord));

        final KeyWord keyWord = new KeyWord();
        keyWord.setTitle("KEY_WORD");
        when(keyWordRepository.findAll()).thenReturn(Collections.singletonList(keyWord));

        when(projectService.getFLProjects(any(), any())).thenReturn(Collections.singletonList(getProject()));

        final RssFeed rssFeed = new RssFeed();
        rssFeed.setTitle("RSS_FEED");

        when(rssFeedRepository.findByType(any())).thenReturn(Collections.singletonList(rssFeed));
        when(projectService.getLastProjectPubDate(any())).thenReturn(123L);

        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    public void testUpwork() throws Exception {
        when(projectService.getUpworkProjects()).thenReturn(Collections.singletonList(getProject()));
        mockMvc.perform(get("/upwork")).andExpect(status().isOk());
    }

    private Project getProject() {

        final Category category = new Category();
        category.setTitle(TEST_CATEGORY);

        final Project project = new Project();
        project.setTitle(TEST_TITLE);
        project.setLink(TEST_LINK);
        project.setDescription(TEST_DESCRIPTION);
        project.setGuid(TEST_GUID);
        project.setCategories(Collections.singletonList(category));
        return project;
    }
}
