package ru.alexgryaznov.fltelegram.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.alexgryaznov.fltelegram.model.Chat;

@Repository
public interface ChatRepository extends CrudRepository<Chat, Long> {
    // empty
}
