package ru.alexgryaznov.flproject.service;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.alexgryaznov.flproject.dao.ProjectRepository;
import ru.alexgryaznov.flproject.dao.RssFeedRepository;
import ru.alexgryaznov.flproject.domain.Project;
import ru.alexgryaznov.flproject.domain.RssFeed;
import ru.alexgryaznov.flproject.domain.RssFeedType;
import ru.alexgryaznov.flproject.domain.StopWord;

import java.util.*;
import java.util.stream.StreamSupport;

@Component
public class ProjectService {

    private static final int STOP_WORD_MATCH_LENGTH = 50;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private RssFeedRepository rssFeedRepository;

    public void updateProjectWasRead(Date endDate) {
        for (Project project : projectRepository.findByWasReadFalseAndPubDateLessThanEqual(endDate)) {
            project.setWasRead(true);
            projectRepository.save(project);
        }
    }

    public Iterable<Project> getFLProjects(Set<StopWord> stopWords, HighlightEngine highlightEngine) {

        final Collection<RssFeed> rssFeeds = rssFeedRepository.findByType(RssFeedType.FL.name());
        final Iterable<Project> projects = projectRepository.findByWasReadFalseAndRssFeedInOrderByPubDateDesc(rssFeeds);

        processStopWordsInProjectTitle(projects, stopWords, highlightEngine);
        processStopWordsInProjectContent(projects, stopWords, highlightEngine);
        return projects;
    }

    public Iterable<Project> getUpworkProjects() {
        final Collection<RssFeed> rssFeeds = rssFeedRepository.findByType(RssFeedType.UPWORK.name());
        return projectRepository.findByWasReadFalseAndRssFeedInOrderByPubDateDesc(rssFeeds);
    }

    public Long getLastProjectPubDate(Iterable<Project> projects) {
        return StreamSupport.stream(projects.spliterator(), false)
                .max(Comparator.comparing(Project::getPubDate))
                .map(Project::getPubDate)
                .map(Date::getTime)
                .orElse(0L);
    }

    private void processStopWordsInProjectTitle(Iterable<Project> projects, Set<StopWord> stopWords, HighlightEngine highlightEngine) {
        projects.forEach(project -> stopWords.forEach(stopWord -> {
            if (project.getTitle().toLowerCase().contains(stopWord.getTitle())) {
                project.setHasStopWordInTitle(true);
                project.setTitle(highlightEngine.highlightStopWord(project.getTitle(), stopWord));
            }
        }));
    }

    private void processStopWordsInProjectContent(Iterable<Project> projects, Set<StopWord> stopWords, HighlightEngine highlightEngine) {
        projects.forEach(project -> {

            final List<String> stopWordMatches = new ArrayList<>();
            stopWords.forEach(stopWord -> {

                final String contentHtml = project.getContent();
                if (contentHtml != null && contentHtml.toLowerCase().contains(stopWord.getTitle())) {

                    final String contentText = Jsoup.parse(contentHtml).text();
                    final String contentTextLowerCase = contentText.toLowerCase();

                    int indexOf = 0;
                    while (indexOf != -1) {
                        indexOf = contentTextLowerCase.indexOf(stopWord.getTitle(), indexOf + 1);
                        if (indexOf != -1) {

                            final int beginIndex = Math.max(0, indexOf - STOP_WORD_MATCH_LENGTH);
                            final int endIndex = Math.min(contentText.length(), indexOf + STOP_WORD_MATCH_LENGTH);

                            final String stopWordMatch = contentText.substring(beginIndex, endIndex);
                            stopWordMatches.add(highlightEngine.highlightStopWord(stopWordMatch, stopWord));
                        }
                    }
                }
            });
            project.setStopWordMatchesInContent(stopWordMatches);
        });
    }

    public interface HighlightEngine {

        String highlightStopWord(String string, StopWord stopWord);
    }
}
