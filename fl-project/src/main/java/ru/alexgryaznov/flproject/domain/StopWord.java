package ru.alexgryaznov.flproject.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "keyword") //TODO rename table and remove table annotation
public class StopWord extends Word {

    public StopWord() {
        // empty
    }

    public StopWord(String title) {
        super(title);
    }
}
