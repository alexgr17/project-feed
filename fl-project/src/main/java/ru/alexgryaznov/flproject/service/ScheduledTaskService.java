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

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ScheduledTaskService {

    private static final Pattern FL_PROJECT_LINK_PATTERN = Pattern.compile(".*/projects/([0-9]*)/.*");

    private static final String DIV_TAG_OPEN = "<div>";
    private static final String DIV_TAG_CLOSE = "</div>";

    private static final String PROJECT_TAG_ID_PREFIX = "#projectp";
    private static final int LOAD_CONTENT_INTERVAL = 2000;
    private static final Random RANDOM = new Random();

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private RssFeedRepository rssFeedRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RssParserService rssParserService;

    private RestTemplate restTemplate;

    public ScheduledTaskService(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    @Scheduled(fixedRate = 300_000)
    private void loadProjects() {

        for (RssFeed rssFeed : rssFeedRepository.findAll()) {

            log.info("Loading projects for rss feed: {}", rssFeed.getTitle());

            final AtomicInteger newProjectsCount = new AtomicInteger();
            final List<Project> projects = rssParserService.loadProjects(rssFeed.getUrl());

            projects.stream()
                    .filter(project -> !projectRepository.findById(project.getGuid()).isPresent())
                    .forEach(project -> {
                        project.setRssFeed(rssFeed);
                        projectRepository.save(project);
                        project.getCategories().forEach(category -> categoryRepository.save(category));
                        newProjectsCount.incrementAndGet();
                    });

            log.info("Projects successfully loaded - total: {}, new: {}", projects.size(), newProjectsCount.get());
        }
    }

    @Scheduled(fixedRate = 300_000)
    private void loadContentForFLProjects() throws InterruptedException {

        final Collection<RssFeed> rssFeeds = rssFeedRepository.findByType(RssFeedType.FL.name());
        final Collection<Project> projects = projectRepository.findByContentIsNullAndRssFeedIn(rssFeeds);

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

        log.info("Content successfully loaded - total: {}", projects.size());
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
