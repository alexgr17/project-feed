package ru.alexgryaznov.flproject.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.alexgryaznov.flproject.domain.KeyWord;

@Repository
public interface KeyWordRepository extends CrudRepository<KeyWord, Integer> {
    // empty
}
