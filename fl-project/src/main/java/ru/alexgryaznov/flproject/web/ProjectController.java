package ru.alexgryaznov.flproject.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.alexgryaznov.flproject.dao.KeyWordRepository;
import ru.alexgryaznov.flproject.dao.RssFeedRepository;
import ru.alexgryaznov.flproject.domain.KeyWord;
import ru.alexgryaznov.flproject.domain.Project;
import ru.alexgryaznov.flproject.domain.StopWord;
import ru.alexgryaznov.flproject.service.ProjectService;
import ru.alexgryaznov.flproject.service.StopWordService;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

//TODO add logic for telegram / java
@Controller
public class ProjectController {

    private final RssFeedRepository rssFeedRepository;
    private final KeyWordRepository keyWordRepository;
    private final ProjectService projectService;
    private final KeyWordHighlightEngine keyWordHighlightEngine;
    private final StopWordHighlightEngine stopWordHighlightEngine;
    private final StopWordService stopWordService;

    @Autowired
    public ProjectController(
            RssFeedRepository rssFeedRepository,
            KeyWordRepository keyWordRepository,
            ProjectService projectService,
            KeyWordHighlightEngine keyWordHighlightEngine,
            StopWordHighlightEngine stopWordHighlightEngine,
            StopWordService stopWordService
    ) {
        this.rssFeedRepository = rssFeedRepository;
        this.keyWordRepository = keyWordRepository;
        this.projectService = projectService;
        this.keyWordHighlightEngine = keyWordHighlightEngine;
        this.stopWordHighlightEngine = stopWordHighlightEngine;
        this.stopWordService = stopWordService;
    }

    @GetMapping("/")
    public String index(ModelMap model) {

        final Set<StopWord> stopWords = stopWordService.getStopWords();
        final Iterable<KeyWord> keyWords = keyWordRepository.findAll();
        final Iterable<Project> projects = projectService.getFLProjects(keyWords, stopWords, keyWordHighlightEngine, stopWordHighlightEngine);

        model.addAttribute("title", "Fl.ru feed");
        model.addAttribute("rssFeeds", rssFeedRepository.findAll());

        model.addAttribute("keyWords", StreamSupport.stream(keyWords.spliterator(), false)
                .map(KeyWord::getTitle)
                .sorted(String::compareTo)
                .collect(Collectors.toList()));

        model.addAttribute("stopWords", stopWords.stream()
                .filter(stopWord -> stopWord.getSort() != null)
                .map(StopWord::getTitle)
                .sorted(String::compareTo)
                .collect(Collectors.toList()));

        model.addAttribute("projects", projects);
        model.addAttribute("lastProjectPubDate", projectService.getLastProjectPubDate(projects));
        return "index";
    }

    @GetMapping("/upwork")
    public String upwork(ModelMap model) {
        model.addAttribute("title", "Upwork feed");
        model.addAttribute("projects", projectService.getUpworkProjects());
        return "index";
    }

    @GetMapping("/mark-all-projects-as-read")
    public String markProjectsAsRead(@RequestParam(defaultValue = "0") long lastProjectPubDate) {
        if (lastProjectPubDate > 0) {
            projectService.updateProjectWasRead(new Date(lastProjectPubDate));
        }
        return "redirect:/";
    }
}
