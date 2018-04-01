package ru.alexgryaznov.flproject.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.alexgryaznov.flproject.domain.Category;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {
    // empty
}
