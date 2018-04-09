package ru.alexgryaznov.flproject.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.alexgryaznov.flproject.dao.CategoryRepository;
import ru.alexgryaznov.flproject.dao.ProjectRepository;
import ru.alexgryaznov.flproject.dao.RssFeedRepository;
import ru.alexgryaznov.flproject.domain.Project;
import ru.alexgryaznov.flproject.domain.RssFeed;
import ru.alexgryaznov.flproject.domain.RssFeedType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Profile("prod")
@Component
public class ScheduledTaskService {

    private static final Pattern FL_PROJECT_LINK_PATTERN = Pattern.compile(".*/projects/([0-9]*)/.*");

    private static final String DIV_TAG_OPEN = "<div>";
    private static final String DIV_TAG_CLOSE = "</div>";

    private static final String PROJECT_TAG_ID_PREFIX = "#projectp";
    private static final int LOAD_CONTENT_INTERVAL = 2000;
    private static final Random RANDOM = new Random();

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final RssFeedRepository rssFeedRepository;
    private final ProjectRepository projectRepository;
    private final CategoryRepository categoryRepository;
    private final RssParserService rssParserService;
    private final ProjectService projectService;
    private final TelegramService telegramService;
    private final RestTemplate restTemplate;

    @Autowired
    public ScheduledTaskService(
            RssFeedRepository rssFeedRepository,
            ProjectRepository projectRepository,
            CategoryRepository categoryRepository,
            RssParserService rssParserService,
            ProjectService projectService,
            TelegramService telegramService,
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.rssFeedRepository = rssFeedRepository;
        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
        this.rssParserService = rssParserService;
        this.projectService = projectService;
        this.telegramService = telegramService;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Scheduled(fixedRate = 300_000)
    public void loadProjects() {

        for (RssFeed rssFeed : rssFeedRepository.findAll()) {

            log.info("Loading projects for rss feed: {}", rssFeed.getTitle());

            final AtomicInteger newProjectsCount = new AtomicInteger();
            final List<Project> projects = rssParserService.loadProjects(rssFeed.getUrl());

            final List<Project> loadedProjects = projects.stream()
                    .filter(project -> !projectRepository.findById(project.getGuid()).isPresent())
                    .peek(project -> {
                        project.setRssFeed(rssFeed);
                        projectRepository.save(project);
                        project.getCategories().forEach(categoryRepository::save);
                        newProjectsCount.incrementAndGet();
                    })
                    .collect(Collectors.toList());

            final List<Project> loadedFlProjects = loadedProjects.stream()
                    .filter(project -> RssFeedType.FL.name().equals(project.getRssFeed().getType()))
                    .collect(Collectors.toList());

            projectService.processWordsInProjectTitle((string, word) -> string, (string, word) -> string, loadedFlProjects);
            sendNotifications(loadedFlProjects);

            log.info("Projects successfully loaded - total: {}, new: {}", projects.size(), newProjectsCount.get());
        }
    }

    @Scheduled(fixedRate = 300_000)
    public void loadContentForFLProjects() throws InterruptedException {

        final Collection<RssFeed> rssFeeds = rssFeedRepository.findByType(RssFeedType.FL.name());
        final List<Project> projects = new ArrayList<>(projectRepository.findByContentIsNullAndRssFeedIn(rssFeeds));

        for (Project project : projects) {

            final String link = project.getLink();

            final String html = restTemplate.getForObject(link, String.class);
            final Document doc = Jsoup.parse(html);

            final Matcher matcher = FL_PROJECT_LINK_PATTERN.matcher(link);
            if (!matcher.find()) {
                throw new IllegalStateException("Invalid link format: " + link);
            }

            final int projectId = Integer.parseInt(matcher.group(1));
            project.setContent(getHtml(doc, PROJECT_TAG_ID_PREFIX + projectId));
            projectRepository.save(project);

            Thread.sleep(LOAD_CONTENT_INTERVAL, RANDOM.nextInt(LOAD_CONTENT_INTERVAL));
        }

        projectService.processWordsInProjectContent((string, word) -> string, (string, word) -> string, projects);
        sendNotifications(projects);

        log.info("Content successfully loaded - total: {}", projects.size());
    }

    private void sendNotifications(List<Project> loadedFlProjects) {
        loadedFlProjects.stream()
                .filter(Project::isHasKeyWord)
                .forEach(telegramService::sendNotification);
    }

    private String getHtml(Document doc, String query) {
        final StringBuilder html = new StringBuilder();
        for (Element element : doc.select(query)) {
            html.append(DIV_TAG_OPEN);
            html.append(element.html());
            html.append(DIV_TAG_CLOSE);
        }
        return html.toString();
    }
}
