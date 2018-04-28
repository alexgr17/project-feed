package ru.alexgryaznov.flproject.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alexgryaznov.flproject.dao.KeyWordRepository;
import ru.alexgryaznov.flproject.domain.KeyWord;

@RestController
@RequestMapping("key-word")
public class KeyWordController extends AbstractController<KeyWord> {

    private final KeyWordRepository keyWordRepository;

    @Autowired
    public KeyWordController(KeyWordRepository keyWordRepository) {
        this.keyWordRepository = keyWordRepository;
    }

    @Override
    protected CrudRepository<KeyWord, Integer> getRepository() {
        return keyWordRepository;
    }
}
