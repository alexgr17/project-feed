package ru.alexgryaznov.flproject.service;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.alexgryaznov.flproject.dao.StopWordRepository;
import ru.alexgryaznov.flproject.domain.StopWord;

import java.util.HashSet;
import java.util.Set;

@Component
public class StopWordService {

    private static final String CHAR_C_ENGLISH = "c";
    private static final String CHAR_C_RUSSIAN = "с";

    @Autowired
    private StopWordRepository stopWordRepository;

    public Set<StopWord> getStopWords() {
        final Set<StopWord> stopWords = Sets.newHashSet(stopWordRepository.findAll());
        addStopWordsWithTypos(stopWords, CHAR_C_ENGLISH, CHAR_C_RUSSIAN);
        addStopWordsWithTypos(stopWords, CHAR_C_RUSSIAN, CHAR_C_ENGLISH);
        return stopWords;
    }

    private void addStopWordsWithTypos(Set<StopWord> stopWords, String charCorrect, String charTypo) {
        new HashSet<>(stopWords).stream()
                .filter(stopWord -> stopWord.getTitle().contains(charCorrect))
                .forEach(stopWord -> {
                    final String titleWithTypo = stopWord.getTitle().replaceAll(charCorrect, charTypo);
                    stopWords.add(new StopWord(titleWithTypo));
                });
    }
}
