package ru.alexgryaznov.flproject.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.alexgryaznov.flproject.dao.RssFeedRepository;
import ru.alexgryaznov.flproject.domain.Project;
import ru.alexgryaznov.flproject.domain.StopWord;
import ru.alexgryaznov.flproject.service.ProjectService;
import ru.alexgryaznov.flproject.service.StopWordService;

import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ProjectController {

    @Autowired
    private RssFeedRepository rssFeedRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectService.HighlightEngine highlightEngine;

    @Autowired
    private StopWordService stopWordService;

    @GetMapping("/")
    public String index(ModelMap model) {

        final Set<StopWord> stopWords = stopWordService.getStopWords();
        final Iterable<Project> projects = projectService.getFLProjects(stopWords, highlightEngine);

        model.addAttribute("title", "Fl.ru feed");
        model.addAttribute("rssFeeds", rssFeedRepository.findAll());

        model.addAttribute("stopWords", stopWords.stream()
                .filter(stopWord -> stopWord.getSort() != null)
                .sorted(Comparator.comparing(StopWord::getTitle))
                .map(StopWord::getTitle)
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
