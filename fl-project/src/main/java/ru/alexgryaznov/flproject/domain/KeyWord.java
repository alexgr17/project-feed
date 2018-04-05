package ru.alexgryaznov.flproject.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "keyword_") //TODO rename table and remove table annotation
public class KeyWord extends Word {

    public KeyWord() {
        // empty
    }

    public KeyWord(String title) {
        super(title);
    }
}
