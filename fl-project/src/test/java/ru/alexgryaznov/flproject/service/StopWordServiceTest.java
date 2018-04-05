package ru.alexgryaznov.flproject.service;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import ru.alexgryaznov.flproject.dao.StopWordRepository;
import ru.alexgryaznov.flproject.domain.StopWord;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StopWordServiceTest {

    private StopWord stopWord1;
    private StopWord stopWord2;
    private StopWord stopWordTypo1;
    private StopWord stopWordTypo2;

    private StopWordRepository repository;
    private StopWordService service;

    @Before
    public void setUp() {
        stopWord1 = new StopWord("test1");
        stopWord2 = new StopWord("test2");
        stopWordTypo1 = new StopWord("1c"); // eng 'c'
        stopWordTypo2 = new StopWord("1с"); // rus 'c'

        repository = mock(StopWordRepository.class);
        service = new StopWordService(repository);
    }

    @Test
    public void testEmptyResult() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        assertEquals(Collections.emptySet(), service.getStopWords());
    }

    @Test
    public void testSimpleResult() {
        when(repository.findAll()).thenReturn(Arrays.asList(stopWord1, stopWord2));
        assertEquals(Sets.newHashSet(stopWord1, stopWord2), service.getStopWords());
    }

    @Test
    public void testTypoResult1() {
        when(repository.findAll()).thenReturn(Collections.singletonList(stopWordTypo1));
        assertEquals(Sets.newHashSet(stopWordTypo1, stopWordTypo2), service.getStopWords());
    }

    @Test
    public void testTypoResult2() {
        when(repository.findAll()).thenReturn(Collections.singletonList(stopWordTypo2));
        assertEquals(Sets.newHashSet(stopWordTypo1, stopWordTypo2), service.getStopWords());
    }
}
