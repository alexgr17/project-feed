package ru.alexgryaznov.flproject.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.alexgryaznov.flproject.domain.RssFeed;

import java.util.Collection;

@Repository
public interface RssFeedRepository extends CrudRepository<RssFeed, Integer> {

    Collection<RssFeed> findByType(String rssFeedType);
}
