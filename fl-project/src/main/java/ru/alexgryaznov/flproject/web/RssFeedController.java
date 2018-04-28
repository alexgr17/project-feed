package ru.alexgryaznov.flproject.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alexgryaznov.flproject.dao.RssFeedRepository;
import ru.alexgryaznov.flproject.domain.RssFeed;

@RestController
@RequestMapping("rss-feed")
public class RssFeedController extends AbstractController<RssFeed> {

    private final RssFeedRepository rssFeedRepository;

    @Autowired
    public RssFeedController(RssFeedRepository rssFeedRepository) {
        this.rssFeedRepository = rssFeedRepository;
    }

    @Override
    protected CrudRepository<RssFeed, Integer> getRepository() {
        return rssFeedRepository;
    }
}
