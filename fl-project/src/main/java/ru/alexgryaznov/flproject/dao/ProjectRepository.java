package ru.alexgryaznov.flproject.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.alexgryaznov.flproject.domain.Project;
import ru.alexgryaznov.flproject.domain.RssFeed;

import java.util.Collection;
import java.util.Date;

@Repository
public interface ProjectRepository extends CrudRepository<Project, String> {

    Collection<Project> findByContentIsNullAndRssFeedIn(Collection<RssFeed> rssFeeds);

    Collection<Project> findByPubDateLessThanEqual(Date endDate);

    Collection<Project> findByRssFeedInOrderByPubDateDesc(Collection<RssFeed> rssFeeds);
}
