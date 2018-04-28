package ru.alexgryaznov.flproject.web;

import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public abstract class AbstractController<T> {

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<T> get() {
        return getRepository().findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public void save(@RequestBody T client) {
        getRepository().save(client);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public void delete(@RequestParam int clientId) {
        getRepository().deleteById(clientId);
    }

    protected abstract CrudRepository<T, Integer> getRepository();
}
