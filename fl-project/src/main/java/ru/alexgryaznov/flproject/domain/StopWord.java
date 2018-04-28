package ru.alexgryaznov.flproject.domain;

import javax.persistence.Entity;

@Entity
public class StopWord extends Word {

    public StopWord() {
        // empty
    }

    public StopWord(String title) {
        super(title);
    }
}
