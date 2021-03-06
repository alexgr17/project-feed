package ru.alexgryaznov.flproject.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.alexgryaznov.flproject.dao.CategoryRepository;
import ru.alexgryaznov.flproject.dao.ProjectRepository;
import ru.alexgryaznov.flproject.dao.RssFeedRepository;
import ru.alexgryaznov.flproject.domain.Project;
import ru.alexgryaznov.flproject.domain.RssFeed;
import ru.alexgryaznov.flproject.domain.RssFeedType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private final RestTemplate externalRestTemplate;

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
        this.externalRestTemplate = restTemplateBuilder.build();
    }

    @Scheduled(fixedRate = 300_000)
    public void loadProjects() {
        for (RssFeed rssFeed : rssFeedRepository.findAll()) {
            log.info("Loading projects for rss feed: {}", rssFeed.getTitle());
            try {
                final AtomicInteger newProjectsCount = new AtomicInteger();
                final List<Project> projects = rssParserService.loadProjects(rssFeed.getUrl());

                final List<Project> loadedProjects = projects.stream()
                        .filter(project -> !projectRepository.findById(project.getGuid()).isPresent())
                        .peek(project -> {
                            //TODO fill this in loadProjects method
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
            } catch (Exception e) {
                log.error("Error while loading projects for rss feed: {}", rssFeed.getTitle(), e);
            }
        }
    }

    @Scheduled(fixedRate = 300_000)
    public void loadContentForFLProjects() {

        final Collection<RssFeed> rssFeeds = rssFeedRepository.findByType(RssFeedType.FL.name());
        final List<Project> projects = new ArrayList<>(projectRepository.findByContentIsNullAndRssFeedIn(rssFeeds));

        for (Project project : projects) {
            log.info("Loading content for project: {}", project.getGuid());
            try {
                final String link = project.getLink();

                final String html = externalRestTemplate.getForObject(link, String.class);
                final Document doc = Jsoup.parse(html);

                final Matcher matcher = FL_PROJECT_LINK_PATTERN.matcher(link);
                if (!matcher.find()) {
                    throw new IllegalStateException("Invalid link format: " + link);
                }

                final int projectId = Integer.parseInt(matcher.group(1));
                project.setContent(getHtml(doc, PROJECT_TAG_ID_PREFIX + projectId));
                projectRepository.save(project);

                projectService.processWordsInProjectContent((string, word) -> string, (string, word) -> string, Collections.singletonList(project));
                sendNotifications(Collections.singletonList(project));

                log.info("Content successfully loaded");
                Thread.sleep(LOAD_CONTENT_INTERVAL, RANDOM.nextInt(LOAD_CONTENT_INTERVAL));
            } catch (Exception e) {
                log.error("Error while loading content for project: {}", project.getGuid(), e);
            }
        }

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
