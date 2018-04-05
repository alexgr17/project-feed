package ru.alexgryaznov.flproject.service;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.alexgryaznov.flproject.dao.StopWordRepository;
import ru.alexgryaznov.flproject.domain.StopWord;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StopWordService {

    private static final String CHAR_C_ENGLISH = "c";
    private static final String CHAR_C_RUSSIAN = "с";

    private final StopWordRepository stopWordRepository;

    @Autowired
    public StopWordService(StopWordRepository stopWordRepository) {
        this.stopWordRepository = stopWordRepository;
    }

    public Set<StopWord> getStopWords() {
        final Set<StopWord> stopWords = Sets.newHashSet(stopWordRepository.findAll());
        stopWords.addAll(getStopWordsWithTypos(stopWords, CHAR_C_ENGLISH, CHAR_C_RUSSIAN));
        stopWords.addAll(getStopWordsWithTypos(stopWords, CHAR_C_RUSSIAN, CHAR_C_ENGLISH));
        return stopWords;
    }

    private Set<StopWord> getStopWordsWithTypos(Set<StopWord> stopWords, String charCorrect, String charTypo) {
        return stopWords.stream()
                .filter(stopWord -> stopWord.getTitle().contains(charCorrect))
                .map(stopWord -> new StopWord(stopWord.getTitle().replaceAll(charCorrect, charTypo)))
                .collect(Collectors.toSet());
    }
}
