package ru.alexgryaznov.flclient.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.alexgryaznov.flclient.domain.Client;

@Repository
public interface ClientRepository extends CrudRepository<Client, Integer> {
    // empty
}
