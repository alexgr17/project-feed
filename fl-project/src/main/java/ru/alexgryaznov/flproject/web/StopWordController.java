package ru.alexgryaznov.flproject.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alexgryaznov.flproject.dao.StopWordRepository;
import ru.alexgryaznov.flproject.domain.StopWord;

@RestController
@RequestMapping("stop-word")
public class StopWordController extends AbstractController<StopWord> {

    private final StopWordRepository stopWordRepository;

    @Autowired
    public StopWordController(StopWordRepository stopWordRepository) {
        this.stopWordRepository = stopWordRepository;
    }

    @Override
    protected CrudRepository<StopWord, Integer> getRepository() {
        return stopWordRepository;
    }
}
