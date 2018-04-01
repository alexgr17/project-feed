package ru.alexgryaznov.flproject.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.alexgryaznov.flproject.domain.StopWord;

@Repository
public interface StopWordRepository extends CrudRepository<StopWord, Integer> {
    // empty
}
