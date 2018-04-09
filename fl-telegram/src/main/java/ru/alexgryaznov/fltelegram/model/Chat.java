package ru.alexgryaznov.fltelegram.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Chat {

    @Id
    private long id;

    private String name;
}
