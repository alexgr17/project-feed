package ru.alexgryaznov.flproject.service;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.alexgryaznov.flproject.dao.KeyWordRepository;
import ru.alexgryaznov.flproject.dao.ProjectRepository;
import ru.alexgryaznov.flproject.dao.RssFeedRepository;
import ru.alexgryaznov.flproject.domain.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

@Component
public class ProjectService {

    private static final int STOP_WORD_MATCH_LENGTH = 50;

    private final ProjectRepository projectRepository;
    private final RssFeedRepository rssFeedRepository;
    private final KeyWordRepository keyWordRepository;
    private final StopWordService stopWordService;

    @Autowired
    public ProjectService(
            ProjectRepository projectRepository,
            RssFeedRepository rssFeedRepository,
            KeyWordRepository keyWordRepository,
            StopWordService stopWordService
    ) {
        this.projectRepository = projectRepository;
        this.rssFeedRepository = rssFeedRepository;
        this.keyWordRepository = keyWordRepository;
        this.stopWordService = stopWordService;
    }

    public void updateProjectWasRead(Date endDate) {
        for (Project project : projectRepository.findByWasReadFalseAndPubDateLessThanEqual(endDate)) {
            project.setWasRead(true);
            projectRepository.save(project);
        }
    }

    public Iterable<Project> getFLProjects(
            HighlightEngine keyWordHighlightEngine,
            HighlightEngine stopWordHighlightEngine
    ) {
        final Collection<RssFeed> rssFeeds = rssFeedRepository.findByType(RssFeedType.FL.name());
        final Iterable<Project> projects = projectRepository.findByWasReadFalseAndRssFeedInOrderByPubDateDesc(rssFeeds);

        processWordsInProjectTitle(keyWordHighlightEngine, stopWordHighlightEngine, projects);
        processWordsInProjectContent(keyWordHighlightEngine, stopWordHighlightEngine, projects);

        return projects;
    }

    public Iterable<Project> getUpworkProjects() {
        final Collection<RssFeed> rssFeeds = rssFeedRepository.findByType(RssFeedType.UPWORK.name());
        return projectRepository.findByWasReadFalseAndRssFeedInOrderByPubDateDesc(rssFeeds);
    }

    public void processWordsInProjectTitle(
            HighlightEngine keyWordHighlightEngine,
            HighlightEngine stopWordHighlightEngine,
            Iterable<Project> projects
    ) {
        final Set<StopWord> stopWords = stopWordService.getStopWords();
        final Iterable<KeyWord> keyWords = keyWordRepository.findAll();

        processWordsInProjectTitle(projects, keyWords, keyWordHighlightEngine, project -> project.setHasKeyWordInTitle(true));
        processWordsInProjectTitle(projects, stopWords, stopWordHighlightEngine, project -> project.setHasStopWordInTitle(true));
    }

    public void processWordsInProjectContent(
            HighlightEngine keyWordHighlightEngine,
            HighlightEngine stopWordHighlightEngine,
            Iterable<Project> projects
    ) {
        final Set<StopWord> stopWords = stopWordService.getStopWords();
        final Iterable<KeyWord> keyWords = keyWordRepository.findAll();

        processWordsInProjectContent(projects, keyWords, keyWordHighlightEngine, Project::setKeyWordMatchesInContent);
        processWordsInProjectContent(projects, stopWords, stopWordHighlightEngine, Project::setStopWordMatchesInContent);
    }

    public Long getLastProjectPubDate(Iterable<Project> projects) {
        return StreamSupport.stream(projects.spliterator(), false)
                .max(Comparator.comparing(Project::getPubDate))
                .map(Project::getPubDate)
                .map(Date::getTime)
                .orElse(0L);
    }

    private <T extends Word> void processWordsInProjectTitle(
            Iterable<Project> projects,
            Iterable<T> words,
            HighlightEngine highlightEngine,
            Consumer<Project> projectConsumer
    ) {
        projects.forEach(project -> StreamSupport.stream(words.spliterator(), false)
                .filter(word -> project.getTitle().toLowerCase().contains(word.getTitle()))
                .forEach(word -> {
                    projectConsumer.accept(project);
                    project.setTitle(highlightEngine.highlightWord(project.getTitle(), word));
                }));
    }

    private <T extends Word> void processWordsInProjectContent(
            Iterable<Project> projects,
            Iterable<T> words,
            HighlightEngine highlightEngine,
            Callback callback
    ) {
        projects.forEach(project -> {

            final List<String> wordMatches = new ArrayList<>();
            words.forEach(word -> {

                final String contentHtml = project.getContent();
                if (contentHtml != null && contentHtml.toLowerCase().contains(word.getTitle())) {

                    final String contentText = Jsoup.parse(contentHtml).text();
                    final String contentTextLowerCase = contentText.toLowerCase();

                    int indexOf = 0;
                    while (indexOf != -1) {
                        indexOf = contentTextLowerCase.indexOf(word.getTitle(), indexOf + 1);
                        if (indexOf != -1) {

                            final int beginIndex = Math.max(0, indexOf - STOP_WORD_MATCH_LENGTH);
                            final int endIndex = Math.min(contentText.length(), indexOf + STOP_WORD_MATCH_LENGTH);

                            final String wordMatch = contentText.substring(beginIndex, endIndex);
                            wordMatches.add(highlightEngine.highlightWord(wordMatch, word));
                        }
                    }
                }
            });
            callback.accept(project, wordMatches);
        });
    }

    private interface Callback {

        void accept(Project project, List<String> wordMatches);
    }

    public interface HighlightEngine {

        String highlightWord(String string, Word word);
    }
}
